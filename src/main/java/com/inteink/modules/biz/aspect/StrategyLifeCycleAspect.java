package com.inteink.modules.biz.aspect;

import com.inteink.common.exception.RRException;
import com.inteink.common.utils.RedisUtils;
import com.inteink.modules.biz.annotation.StrategyLifeCycleCheck;
import com.inteink.modules.biz.constant.LiftingStrategyConstant;
import com.inteink.modules.biz.constant.StrategyRedisKeys;
import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.service.validator.LiftingStrategyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 策略生命周期校验AOP
 * 适配项目规范：Redis工具类调用符合项目定义；枚举/常量引用匹配；参数提取逻辑兼容DTO/基础类型
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class StrategyLifeCycleAspect implements Ordered {

    private final BizLiftingStrategyMapper strategyMapper;
    private final LiftingStrategyValidator strategyValidator;
    private final RedisUtils redisUtils;

    // 缓存配置：复用项目Redis工具类的默认过期时间，空值缓存5分钟（防穿透）
    private final long CACHE_EXPIRE = RedisUtils.DEFAULT_EXPIRE;
    private final long NULL_CACHE_EXPIRE = 300;
    // 空值缓存占位符（改用具体字符串，避免空字符串序列化问题）
    private static final String NULL_CACHE_PLACEHOLDER = "NULL_STRATEGY";

    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyLifeCycleCheck)")
    public void lifeCycleCheckPointCut() {}

    @Around("lifeCycleCheckPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 1. 提取策略ID（兼容Long类型入参/含getStrategyId的DTO）
        Long strategyId = extractStrategyId(point.getArgs());
        if (strategyId == null || strategyId <= 0) {
            throw new RRException("策略ID不能为空且必须为正数", 400);
        }

        // 2. 构建缓存Key（复用项目常量类）
        String cacheKey = StrategyRedisKeys.getStrategyInfoKey(strategyId);
        BizLiftingStrategy strategy = null;

        // 3. 优先从Redis获取缓存（仅用RedisUtils公开方法）
        if (redisUtils.hasKey(cacheKey)) {
            Object cacheValue = redisUtils.getKeyValue(cacheKey);
            // 核心修复：判断是否为空值占位符，避免反序列化null报错
            if (NULL_CACHE_PLACEHOLDER.equals(cacheValue)) {
                log.info("策略空值缓存命中 → KEY:{}", cacheKey);
                strategy = null;
            } else {
                strategy = redisUtils.get(cacheKey, BizLiftingStrategy.class);
                log.info("策略缓存命中 → KEY:{}", cacheKey);
            }
        } else {
            log.info("策略缓存未命中，查询数据库 → KEY:{}", cacheKey);
            // 4. 数据库查询
            strategy = strategyMapper.selectById(strategyId);

            // 5. 防缓存穿透：空值缓存（仅用RedisUtils公开方法，规避private属性）
            if (strategy == null) {
                // 步骤1：用公开的setKeyValue写入占位符（绕开toJson序列化）
                redisUtils.setKeyValue(cacheKey, NULL_CACHE_PLACEHOLDER);
                // 步骤2：用公开方法设置过期时间（先检查Key存在，再设置）
                if (redisUtils.hasKey(cacheKey)) {
                    // 循环重试设置过期时间（确保生效）
                    int retry = 3;
                    while (retry > 0) {
                        Long expire = redisUtils.getExpire(cacheKey);
                        if (expire == null || expire <= 0) {
                            // 调用RedisUtils的set方法（带过期时间）覆盖，确保过期时间生效
                            redisUtils.set(cacheKey, NULL_CACHE_PLACEHOLDER, NULL_CACHE_EXPIRE);
                            retry--;
                        } else {
                            break;
                        }
                    }
                }

                // 新增：延迟100ms再验证（避免Redis写入异步延迟）
                Thread.sleep(100);
                // 仅用公开的hasKey验证写入状态
                boolean isWriteSuccess = redisUtils.hasKey(cacheKey);
                log.info("策略空值缓存写入 → KEY:{}, 过期时间:{}s, 实际写入状态:{}",
                        cacheKey, NULL_CACHE_EXPIRE, isWriteSuccess);
            } else {
                // 6. 写入有效缓存（保持原有逻辑）
                redisUtils.set(cacheKey, strategy, CACHE_EXPIRE);
                log.info("策略缓存写入成功 → KEY:{}, 过期时间:{}s", cacheKey, CACHE_EXPIRE);
            }
        }

        // 7. 基础校验（存在+未删除）
        strategyValidator.validateStrategyExistAndNotDeleted(strategy, strategyId);

        // 8. 生命周期规则校验
        String methodName = point.getSignature().getName();
        validateLifeCycleRule(methodName, strategy);

        // 9. 放行执行业务逻辑
        return point.proceed();
    }

    /**
     * 生命周期规则校验（适配项目枚举/常量）
     */
    private void validateLifeCycleRule(String methodName, BizLiftingStrategy strategy) {
        Long status = strategy.getStrategyStatus();
        Integer checkState = strategy.getStrategyCheckState();

        // 审核操作：仅待审核状态可操作
        if ("auditStrategy".equals(methodName)) {
            if (!Objects.equals(checkState, LiftingStrategyConstant.CHECK_STATE_PENDING)) {
                String desc = BizLiftingStrategyEnum.getCheckStateDesc(checkState);
                throw new RRException("策略当前为【" + desc + "】状态，禁止重复审核", 400);
            }
        }

        // 暂停/恢复操作：仅有效状态可操作
        if ("pauseStrategy".equals(methodName) || "resumeStrategy".equals(methodName)) {
            if (!Objects.equals(status, LiftingStrategyConstant.STRATEGY_STATUS_VALID)) {
                throw new RRException("策略当前为【无效】状态，禁止执行暂停/恢复操作", 400);
            }
        }

        // 修改操作：仅待审核状态可操作
        if ("updateStrategyWithDetail".equals(methodName)) {
            if (!Objects.equals(checkState, LiftingStrategyConstant.CHECK_STATE_PENDING)) {
                String desc = checkState == LiftingStrategyConstant.CHECK_STATE_PASS ? "审核通过" : "审核不通过";
                throw new RRException("策略当前为【" + desc + "】状态，禁止修改", 400);
            }
        }

        // 执行操作：有效+审核通过
        if ("executeStrategy".equals(methodName)) {
            if (!Objects.equals(status, LiftingStrategyConstant.STRATEGY_STATUS_VALID)) {
                throw new RRException("策略当前为【无效】状态，禁止执行", 400);
            }
            if (!Objects.equals(checkState, LiftingStrategyConstant.CHECK_STATE_PASS)) {
                String desc = checkState == LiftingStrategyConstant.CHECK_STATE_PENDING ? "待审核" : "审核不通过";
                throw new RRException("策略当前为【" + desc + "】状态，禁止执行", 400);
            }
        }
    }

    /**
     * 提取策略ID（兼容Long/DTO）
     */
    private Long extractStrategyId(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
            try {
                return (Long) arg.getClass().getMethod("getStrategyId").invoke(arg);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    /**
     * AOP执行优先级：最高（最先执行校验）
     */
    @Override
    public int getOrder() {
        return 10;
    }
}