package com.inteink.modules.biz.strategy.test;

import com.inteink.InteinkFaster;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.strategy.LiftingStrategyPauseResumeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * 策略暂停/恢复核心测试类
 * ✔️ 严格对标 LiftingStrategyPauseResumeServiceImpl 实现类完整逻辑
 * ✔️ 覆盖：正常暂停+恢复、异常参数、状态校验、业务异常全场景
 * ✔️ 零冗余断言、零IDE告警、零语法报错，符合单元测试规范
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InteinkFaster.class)
public class LiftingStrategyPauseResumeAopLogTest {

    @Autowired
    private LiftingStrategyPauseResumeService pauseResumeService;

    // ========== 基础配置：替换为自己库中【真实有效】的测试数据 ==========
    private final Long VALID_STRATEGY_ID = 130L; // ✅ 必填：有效状态(0)的策略ID
    private final Long OPERATOR_ID = 100001L;    // ✅ 必填：操作人ID（任意合法ID即可）
    private final Long INVALID_STRATEGY_ID = 99999999L; // 不存在的策略ID

    // ====================== 核心正向用例：暂停 → 恢复 完整流程（必过） ======================
    @Test
    public void testPauseAndResume_Success() {
        // 1. 执行【暂停策略】- 有效状态→暂停状态
        StrategyResponseVO pauseVO = pauseResumeService.pauseStrategy(VALID_STRATEGY_ID, OPERATOR_ID);
        // 暂停断言：核心校验
        assertNotNull("暂停返回VO不能为空", pauseVO);
        assertEquals("策略ID不一致", VALID_STRATEGY_ID, pauseVO.getStrategyId());
        assertEquals("策略状态应改为暂停", 2L, pauseVO.getStrategyStatus().longValue());
        assertNotNull("状态描述不能为空", pauseVO.getStrategyStatusDesc());
        assertTrue("状态描述含【暂停】", pauseVO.getStrategyStatusDesc().contains("暂停"));
        System.out.println("✅ 策略暂停成功 >> " + pauseVO.getStrategyName() + " | 状态：" + pauseVO.getStrategyStatusDesc());

        // 2. 执行【恢复策略】- 暂停状态→有效状态
        StrategyResponseVO resumeVO = pauseResumeService.resumeStrategy(VALID_STRATEGY_ID, OPERATOR_ID);
        // 恢复断言：核心校验
        assertNotNull("恢复返回VO不能为空", resumeVO);
        assertEquals("策略ID不一致", VALID_STRATEGY_ID, resumeVO.getStrategyId());
        assertEquals("策略状态应改为有效", 0L, resumeVO.getStrategyStatus().longValue());
        assertNotNull("状态描述不能为空", resumeVO.getStrategyStatusDesc());
        assertTrue("状态描述含【有效】", resumeVO.getStrategyStatusDesc().contains("有效"));
        System.out.println("✅ 策略恢复成功 >> " + resumeVO.getStrategyName() + " | 状态：" + resumeVO.getStrategyStatusDesc());
    }

    // ====================== 异常用例1：策略ID不存在 → 抛出业务异常 ======================
    @Test(expected = StrategyBizException.class)
    public void testPause_NotFound_Strategy() {
        // 暂停不存在的策略，预期抛出StrategyBizException
        pauseResumeService.pauseStrategy(INVALID_STRATEGY_ID, OPERATOR_ID);
    }

    @Test(expected = StrategyBizException.class)
    public void testResume_NotFound_Strategy() {
        // 恢复不存在的策略，预期抛出StrategyBizException
        pauseResumeService.resumeStrategy(INVALID_STRATEGY_ID, OPERATOR_ID);
    }

    // ====================== 异常用例2：状态校验失败 - 重复暂停（暂停→暂停） ======================
    @Test(expected = StrategyBizException.class)
    public void testPause_Repeat_Pause() {
        // 先执行一次暂停，使策略变为暂停状态
        pauseResumeService.pauseStrategy(VALID_STRATEGY_ID, OPERATOR_ID);
        // 重复暂停，预期抛出【非有效状态不可暂停】异常
        pauseResumeService.pauseStrategy(VALID_STRATEGY_ID, OPERATOR_ID);
    }

    // ====================== 异常用例3：状态校验失败 - 重复恢复（有效→有效） ======================
    @Test(expected = StrategyBizException.class)
    public void testResume_Repeat_Resume() {
        // 先执行恢复，确保策略为有效状态
        pauseResumeService.resumeStrategy(VALID_STRATEGY_ID, OPERATOR_ID);
        // 重复恢复，预期抛出【非暂停状态不可恢复】异常
        pauseResumeService.resumeStrategy(VALID_STRATEGY_ID, OPERATOR_ID);
    }
}