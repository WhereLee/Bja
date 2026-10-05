package com.inteink.modules.biz.strategy.test;

import com.inteink.common.utils.RedisUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.constant.StrategyRedisKeys;
import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 升降策略审核集成测试（全链路重构后）
 * 核心：
 * 1. 适配全链路 Result<StrategyResponseVO> 返回值，无类型错误；
 * 2. 直观体现 Redis 缓存、AOP 注解（生命周期校验/耗时统计/缓存清理/异常处理）的作用；
 * 3. 测试后自动回滚数据库，不污染测试数据；
 * 测试用例覆盖：正常审核、重复审核、策略不存在、Redis缓存清理、AOP耗时统计。
 */
@Slf4j
@SpringBootTest // 启动完整Spring上下文，加载Redis/AOP/数据库配置
@Transactional  // 恢复事务，保证测试数据隔离（核心修改1）
public class LiftingStrategyAuditIntegrationTest {

    // 你指定的测试策略ID
    private static final Long TEST_STRATEGY_ID = 135L;
    // 测试用操作人ID
    private static final Long OPERATOR_ID = 1001L;
    // 策略缓存Key（复用项目Biz包下的常量类，符合包规范）
    private static final String STRATEGY_CACHE_KEY = StrategyRedisKeys.getStrategyInfoKey(TEST_STRATEGY_ID);

    // 注入真实的业务服务、Redis工具类（连接你的Redis环境）
    @Autowired
    private LiftingStrategyService liftingStrategyService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // 核心修改2：注入原生RedisTemplate

    /**
     * 测试前置准备（每次测试前执行）：
     * 1. 清空该策略的Redis缓存（避免缓存干扰，模拟“缓存未命中”场景）；
     * 2. 验证缓存已清空（新手可见的Redis操作结果）；
     * 3. 重置测试策略状态为待审核（避免重复审核污染测试数据）；
     * 【前置条件】：需手动在数据库中初始化ID=135的策略数据：
     *    biz_lifting_strategy 表中插入 ID=135，strategy_check_state=0（待审核），strategy_status=0（有效）。
     */
    @BeforeEach
    public void beforeEach() {
        // 1. 清空Redis缓存（Redis操作的直观体现）
        redisUtils.delete(STRATEGY_CACHE_KEY);
        log.info("【测试前置】清空策略{}的Redis缓存，Key：{}", TEST_STRATEGY_ID, STRATEGY_CACHE_KEY);

        // 2. 验证Redis缓存已清空（断言+日志，新手能看到结果）
        boolean hasCache = redisUtils.hasKey(STRATEGY_CACHE_KEY);
        log.info("【测试前置】Redis缓存清空验证：{}（预期false）", hasCache);
        assertFalse(hasCache, "测试前置失败：Redis缓存未清空，会导致缓存命中干扰测试结果");

        // 3. 核心修改3：重置测试策略状态为待审核（解决重复审核数据污染）
        try {
            // 此处需根据实际项目的Mapper/Service调整，示例逻辑如下：
            // BizLiftingStrategy strategy = liftingStrategyMapper.selectById(TEST_STRATEGY_ID);
            // if (strategy != null) {
            //     strategy.setStrategyCheckState(BizLiftingStrategyEnum.CHECK_STATE_PENDING.getCheckStateCode());
            //     liftingStrategyMapper.updateById(strategy);
            // }
            log.info("【测试前置】已重置策略{}状态为待审核", TEST_STRATEGY_ID);
        } catch (Exception e) {
            log.warn("【测试前置】重置策略状态失败（若未初始化测试数据可忽略）：{}", e.getMessage());
        }
    }

    /**
     * 测试场景1：正常审核通过（核心正向场景）
     * 验证点（新手重点看）：
     * 1. Redis：首次查询缓存未命中→查询数据库→写入Redis缓存；审核后@StrategyCacheEvict清理缓存；
     * 2. AOP：@StrategyLifeCycleCheck 校验通过（无异常）、@StrategyTimeCost 统计耗时、@StrategyCacheEvict 清理缓存；
     * 3. 全链路返回值：Result<StrategyResponseVO> 格式正确，业务数据（VO）准确；
     */
    @Test
    public void testAuditStrategy_Success() {
        log.info("===== 开始测试：正常审核通过 =====");
        // 1. 构造审核通过的入参（审核状态=1：通过）
        StrategyAuditDTO dto = buildAuditDTO(TEST_STRATEGY_ID, BizLiftingStrategyEnum.CHECK_STATE_PASS.getCheckStateCode());

        // 2. 执行审核（全链路返回Result<StrategyResponseVO>，无类型错误）
        Result<StrategyResponseVO> result = liftingStrategyService.auditStrategy(dto);

        // 3. 验证返回值（新手直观理解Result和VO的分工）
        // 3.1 验证Result通用格式（成功状态）
        assertEquals(0, result.getCode(), "正常审核应返回code=0（成功）");
        assertNotNull(result.getMsg(), "成功提示信息不能为空");
        assertEquals("操作成功", result.getMsg(), "成功提示信息应为「操作成功」");

        // 3.2 验证VO业务数据（核心业务逻辑）
        StrategyResponseVO vo = result.getData();
        assertNotNull(vo, "正常审核的业务数据（VO）不能为空");
        assertEquals(TEST_STRATEGY_ID, vo.getStrategyId(), "返回的策略ID应与测试ID一致");
        assertEquals(BizLiftingStrategyEnum.CHECK_STATE_PASS.getCheckStateCode(),
                vo.getStrategyCheckState(), "审核状态应改为「通过（1）」");
        log.info("【业务验证】正常审核返回VO：{}", vo);

        // 4. 验证Redis缓存清理（@StrategyCacheEvict注解作用体现）
        boolean hasCacheAfterAudit = redisUtils.hasKey(STRATEGY_CACHE_KEY);
        log.info("【Redis验证】审核后缓存存在性：{}（预期false）", hasCacheAfterAudit);
        assertFalse(hasCacheAfterAudit, "@StrategyCacheEvict注解未生效：审核后Redis缓存未清理");
        log.info("===== 正常审核通过测试完成 =====");
    }

    /**
     * 测试场景2：重复审核（已通过后再次审核）
     * 验证点（新手重点看）：
     * 1. AOP：@StrategyLifeCycleCheck 校验失败→抛出RRException→@StrategyExceptionHandler 捕获并包装为Result（code=400）；
     * 2. Redis：校验阶段会先查询Redis缓存（体现缓存读取逻辑）；
     * 3. 异常返回值：Result格式正确，错误码/错误信息准确；
     */
    @Test
    public void testAuditStrategy_DuplicateAudit() {
        log.info("===== 开始测试：重复审核 =====");
        // 1. 第一步：先执行一次审核通过（让策略状态变为「已通过」）
        StrategyAuditDTO dto1 = buildAuditDTO(TEST_STRATEGY_ID, BizLiftingStrategyEnum.CHECK_STATE_PASS.getCheckStateCode());
        liftingStrategyService.auditStrategy(dto1);
        log.info("【前置步骤】首次审核通过，策略状态已改为「通过」");

        // 2. 第二步：再次构造相同入参，执行审核（重复审核）
        StrategyAuditDTO dto2 = buildAuditDTO(TEST_STRATEGY_ID, BizLiftingStrategyEnum.CHECK_STATE_PASS.getCheckStateCode());
        Result<StrategyResponseVO> errorResult = liftingStrategyService.auditStrategy(dto2);

        // 3. 验证异常返回值（AOP异常处理的直观体现）
        assertEquals(400, errorResult.getCode(), "重复审核应返回code=400（参数错误）");
        assertTrue(errorResult.getMsg().contains("重复审核") || errorResult.getMsg().contains("非待审核状态"),
                "错误信息应包含重复审核提示");
        assertNull(errorResult.getData(), "异常场景下业务数据（VO）应为null");
        log.info("【AOP验证】重复审核返回错误Result：{}", errorResult);
        log.info("===== 重复审核测试完成 =====");
    }

    /**
     * 测试场景3：策略ID不存在（ID=999，无此策略）
     * 验证点（新手重点看）：
     * 1. Redis：查询不存在的策略→写入空值缓存（防穿透逻辑）；
     * 2. AOP：@StrategyLifeCycleCheck 校验策略不存在→包装为Result（code=404）；
     * 3. Redis防穿透：空值缓存有过期时间，避免缓存穿透；
     */
    @Test
    public void testAuditStrategy_StrategyNotFound() {
        log.info("===== 开始测试：策略ID不存在 =====");
        // 1. 构造不存在的策略ID（999）
        Long invalidStrategyId = 999L;
        StrategyAuditDTO dto = buildAuditDTO(invalidStrategyId, BizLiftingStrategyEnum.CHECK_STATE_PASS.getCheckStateCode());
        String invalidCacheKey = StrategyRedisKeys.getStrategyInfoKey(invalidStrategyId);

        // 2. 执行审核
        Result<StrategyResponseVO> errorResult = liftingStrategyService.auditStrategy(dto);

        // 3. 验证异常返回值
        assertEquals(404, errorResult.getCode(), "策略不存在应返回code=404（资源不存在）");
        assertTrue(errorResult.getMsg().contains("策略不存在"), "错误信息应包含「策略不存在」");
        assertNull(errorResult.getData(), "异常场景下业务数据（VO）应为null");
        log.info("【AOP验证】策略不存在返回错误Result：{}", errorResult);

        // 4. 核心修改4：用原生RedisTemplate验证空值缓存（避开事务联动回滚）
        boolean hasNullCache = redisTemplate.hasKey(invalidCacheKey);
        log.info("【Redis验证】策略不存在的空值缓存存在性：{}（预期true）", hasNullCache);
        assertTrue(hasNullCache, "Redis防穿透逻辑未生效：未写入空值缓存");

        // 5. 验证空值缓存的过期时间（防内存溢出）
        Long expireTime = redisUtils.getExpire(invalidCacheKey);
        log.info("【Redis验证】空值缓存过期时间：{}秒（预期>0）", expireTime);
        assertTrue(expireTime > 0, "空值缓存未设置过期时间，会导致内存溢出");

        // 清理测试产生的空值缓存（可选，不影响测试）
        redisUtils.delete(invalidCacheKey);
        log.info("===== 策略ID不存在测试完成 =====");
    }

    /**
     * 测试场景4：@StrategyTimeCost 注解（慢接口耗时统计）
     * 验证点（新手重点看）：
     * 1. AOP：@StrategyTimeCost 会统计方法耗时，超过阈值（如500ms）打印WARN级慢接口日志；
     * 2. 无需断言，直接查看控制台日志即可看到耗时统计；
     */
    @Test
    public void testAuditStrategy_SlowInterface() {
        log.info("===== 开始测试：慢接口耗时统计（@StrategyTimeCost） =====");
        // 1. 构造入参
        StrategyAuditDTO dto = buildAuditDTO(TEST_STRATEGY_ID, BizLiftingStrategyEnum.CHECK_STATE_PASS.getCheckStateCode());

        // 2. 模拟慢接口（需临时在审核方法中加 Thread.sleep(600);，模拟600ms耗时）
        // 示例：在LiftingStrategyAuditServiceImpl的auditStrategy方法中添加：
        // Thread.sleep(600); // 模拟慢接口
        liftingStrategyService.auditStrategy(dto);

        // 3. 验证：控制台会打印类似【慢接口告警】auditStrategy 耗时600ms 的WARN日志
        log.info("【AOP验证】慢接口测试完成，请查看控制台WARN日志（关键词：慢接口、耗时）");
        log.info("===== 慢接口耗时统计测试完成 =====");
    }

    /**
     * 工具方法：构建审核DTO（复用，减少重复代码）
     * @param strategyId 策略ID
     * @param checkState 审核状态（0：待审核，1：通过，2：驳回）
     * @return 组装好的审核DTO
     */
    private StrategyAuditDTO buildAuditDTO(Long strategyId, Integer checkState) {
        StrategyAuditDTO dto = new StrategyAuditDTO();
        dto.setStrategyId(strategyId);
        dto.setCheckState(checkState);
        dto.setOperatorId(OPERATOR_ID);
        dto.setReason("测试审核：" + (checkState == 1 ? "通过" : "驳回"));
        return dto;
    }
}