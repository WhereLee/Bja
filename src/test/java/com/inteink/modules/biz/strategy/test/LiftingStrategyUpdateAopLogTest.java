package com.inteink.modules.biz.strategy.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.InteinkFaster;
import com.inteink.common.exception.RRException;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyLogService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyUpdateService;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InteinkFaster.class)
@Transactional // 自动回滚，无数据污染
public class LiftingStrategyUpdateAopLogTest {

    @Autowired
    private LiftingStrategyUpdateService liftingStrategyUpdateService;
    @Autowired
    private BizLiftingStrategyLogService bizLiftingStrategyLogService;

    @Test
    public void testUpdateStrategyAndAopLog() {
        // 1. 构造入参（替换为你的真实策略ID）
        StrategyUpdateDTO updateDTO = new StrategyUpdateDTO();
        updateDTO.setStrategyId(135L);  // ✅ 你的测试ID：135
        updateDTO.setStrategyName("修改测试-AOP日志验证001");
        updateDTO.setStrategyAction(2);
        updateDTO.setStrategyType(1);
        updateDTO.setStrategyDates("09:00");
        updateDTO.setDetailTime("10:00,18:00");
        updateDTO.setRodIds("1,2");
        updateDTO.setStrategyUpdater(100001L);

        // 2. 执行业务
        StrategyResponseVO responseVO = null;
        try {
            responseVO = liftingStrategyUpdateService.updateStrategyWithDetail(updateDTO);
        } catch (StrategyBizException e) {
            fail("【主流程失败】策略修改异常：" + e.getMessage());
        }

        // 3. 基础校验
        assertNotNull("修改返回VO为空", responseVO);
        assertNotNull("策略ID为空", responseVO.getStrategyId());
        assertEquals("入参ID与返回ID不一致", updateDTO.getStrategyId(), responseVO.getStrategyId());
        System.out.println("✅ 策略修改成功：strategyId = " + responseVO.getStrategyId());

        // 4. ✅ 核心校验：AOP日志落库（新增容错，适配修复后逻辑）
        LambdaQueryWrapper<BizLiftingStrategyLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(BizLiftingStrategyLog::getStrategyId, responseVO.getStrategyId());
        logWrapper.eq(BizLiftingStrategyLog::getLogType, 2); // 修改日志类型=2
//        logWrapper.orderByDesc(BizLiftingStrategyLog::getLogCreateTime); // 按时间倒序，取最新日志

        BizLiftingStrategyLog strategyLog = bizLiftingStrategyLogService.getOne(logWrapper);

        // 日志强校验（必过）
        assertNotNull("❌【核心失败】AOP切面未生效，修改日志未生成", strategyLog);
        assertEquals("❌ 日志策略ID关联错误", responseVO.getStrategyId(), strategyLog.getStrategyId());
        assertEquals("❌ 操作人匹配错误", updateDTO.getStrategyUpdater(), strategyLog.getLogOperator());
        assertFalse("❌ 日志内容异常", strategyLog.getLogRemark().contains("未知"));

        // 测试通过打印
        System.out.println("✅【修改AOP日志测试全过】策略ID：" + responseVO.getStrategyId());
        System.out.println("✅ AOP日志详情：" + strategyLog.getLogRemark());
        assertTrue(true);
    }

    @Test
    public void testUpdateStrategyWithInvalidParam() {
        StrategyUpdateDTO updateDTO = new StrategyUpdateDTO();
        updateDTO.setStrategyId(null);
        updateDTO.setStrategyName("异常测试-无ID");
        updateDTO.setStrategyUpdater(100001L);

        boolean isThrowBizException = false;
        String realExceptionMsg = "";
        try {
            liftingStrategyUpdateService.updateStrategyWithDetail(updateDTO);
        } catch (RRException e) { // ← 就改这1行！把StrategyBizException换成RRException
            isThrowBizException = true;
            realExceptionMsg = e.getMessage();
        }
        assertTrue("✅【异常测试通过】已抛出指定业务异常，信息：" + realExceptionMsg, isThrowBizException);
    }
}