package com.inteink.modules.biz.strategy.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.InteinkFaster;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyLogService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyRemoveService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InteinkFaster.class)
@Transactional // 自动回滚，零数据污染
public class LiftingStrategyRemoveAopLogTest {

    @Autowired
    private LiftingStrategyRemoveService liftingStrategyRemoveService;
    @Autowired
    private BizLiftingStrategyLogService bizLiftingStrategyLogService;

    // ========== 主测试：删除成功 + AOP日志生成验证 ==========
    @Test
    public void testRemoveStrategyAndAopLog() {
        StrategyResponseVO responseVO = null;
        // ========== 请替换为你的库中【有效、未删除】的策略ID ==========
        Long TEST_VALID_STRATEGY_ID = 135L;
        try {
            responseVO = liftingStrategyRemoveService.removeStrategyWithDetail(TEST_VALID_STRATEGY_ID);
        } catch (StrategyBizException e) {
            fail("【主流程失败】策略删除异常：" + e.getMessage());
        }

        // 核心校验（无getMsg、无语法错误）
        assertNotNull("返回VO不能为空", responseVO);
        assertNotNull("策略ID不能为空", responseVO.getStrategyId());
        assertEquals("策略ID不一致", TEST_VALID_STRATEGY_ID, responseVO.getStrategyId());
        assertEquals("状态未更新为删除", 1L, responseVO.getStrategyStatus().longValue());
        System.out.println("✅ 策略删除业务执行成功 >> ID = " + responseVO.getStrategyId());

        // AOP日志校验
        LambdaQueryWrapper<BizLiftingStrategyLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(BizLiftingStrategyLog::getStrategyId, responseVO.getStrategyId());
        Integer LOG_TYPE_REMOVE = 7;
        logWrapper.eq(BizLiftingStrategyLog::getLogType, LOG_TYPE_REMOVE);
        BizLiftingStrategyLog strategyLog = bizLiftingStrategyLogService.getOne(logWrapper);

        assertNotNull("❌ AOP日志生成失败", strategyLog);
        System.out.println("✅【全过】策略删除成功 | AOP日志ID = " + strategyLog.getLogId());
    }

    // ========== 异常测试：策略不存在场景 ==========
    @Test
    public void testRemoveStrategyWithInvalidParam() {
        boolean isThrowBizException = false;
        try {
            Long TEST_INVALID_STRATEGY_ID = 999999L;
            liftingStrategyRemoveService.removeStrategyWithDetail(TEST_INVALID_STRATEGY_ID);
        } catch (StrategyBizException e) {
            isThrowBizException = true;
        }
        assertTrue("✅ 异常测试通过：成功抛出StrategyBizException", isThrowBizException);
    }
}