package com.inteink.modules.biz.aspect;

import com.inteink.common.utils.RedisUtils;
import com.inteink.modules.biz.constant.StrategyRedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 策略缓存清理AOP
 * 拦截@StrategyCacheEvict注解，自动提取策略ID并删除缓存，保障缓存一致性
 */
@Slf4j
@Aspect
@Component
public class StrategyCacheEvictAspect {

    @Autowired
    private RedisUtils redisUtils;

    // 切入点：拦截所有标注了@StrategyCacheEvict的方法
    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyCacheEvict)")
    public void cacheEvictPointCut() {}

    @Around("cacheEvictPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 1. 执行原业务方法（先操作数据库，后清理缓存）
        Object result = point.proceed();

        // 2. 提取入参中的策略ID
        Long strategyId = extractStrategyId(point.getArgs());
        if (strategyId != null && strategyId > 0) {
            String cacheKey = StrategyRedisKeys.getStrategyInfoKey(strategyId);
            // 3. 删除对应缓存
            redisUtils.delete(cacheKey);
            log.info("策略缓存清理成功 → KEY:{}", cacheKey);
        }

        return result;
    }

    /**
     *
     * @param args
     * @return
     * 她在想，那场在大雪里的爱意倾诉，是白天还是晚上？或许是晚上，在一盏白光惨淡的路灯下，他们向彼此倾诉着爱意。
     */
    // 复用状态校验AOP的ID提取逻辑，完全适配项目入参场景
    private Long extractStrategyId(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        for (Object arg : args) {
            // 适配入参直接是Long类型的策略ID
            if (arg instanceof Long) {
                return (Long) arg;
            }
            // 适配所有含getStrategyId()方法的DTO入参
            try {
                return (Long) arg.getClass().getMethod("getStrategyId").invoke(arg);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }
}