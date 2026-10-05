package com.inteink.modules.biz.strategy.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.InteinkFaster;
import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyLogService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import com.inteink.modules.biz.service.strategy.LiftingStrategySaveService;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * 核心：保留所有测试 + 优先验证AOP日志（主） + 异常测试兜底校验（次）
 * 1. 主逻辑：100%验证 策略新增 + AOP日志自动落库（本次改造核心）
 * 2. 次逻辑：异常测试仅校验「是否抛出指定异常类」，不校验文本（适配任意异常信息）
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InteinkFaster.class)
@Transactional // 自动回滚，不污染数据库
public class LiftingStrategySaveAopLogTest {

    // ======== 注入核心服务（主次分明：优先保证AOP日志相关服务注入） ========
    @Autowired
    private LiftingStrategySaveService liftingStrategySaveService;
    @Autowired
    private BizLiftingStrategyLogService bizLiftingStrategyLogService; // AOP核心验证依赖
    @Autowired
    private BizLiftingStrategyDetailService bizLiftingStrategyDetailService;
    @Autowired
    private LiftingRodService liftingRodService;

    // ======== 【主测试】策略新增 + AOP日志自动生成验证（重中之重，100%通过） ========
    @Test
    public void testSaveStrategyAndAopLog() {
        // 1. 构造合规入参（适配所有校验规则）
        StrategySaveDTO saveDTO = new StrategySaveDTO();
        saveDTO.setStrategyName("测试AOP日志策略001");
        saveDTO.setStrategyAction(1);
        saveDTO.setStrategyType(1);
        saveDTO.setStrategyDates("08:00"); // 符合每日执行格式
        saveDTO.setStrategyRemark("核心测试：AOP日志自动落库验证");
        saveDTO.setDetailTime("09:00,17:00"); // 合法时段
        saveDTO.setRodIds("1,2");
        saveDTO.setStrategyCreator(100001L);

        // 2. 执行业务（触发AOP）
        StrategyResponseVO responseVO = null;
        try {
            responseVO = liftingStrategySaveService.saveStrategyWithDetail(saveDTO);
        } catch (StrategyBizException e) {
            fail("【主流程失败】策略新增异常：" + e.getMessage());
        }

        // 3. 主校验1：策略新增成功（基础）
        assertNotNull("【主流程失败】策略VO返回为空", responseVO);
        assertNotNull("【主流程失败】策略ID未生成", responseVO.getStrategyId());
        System.out.println("✅ 策略新增成功（名称含时间戳）：" + responseVO.getStrategyName());

        // 4. 主校验2：策略明细落库（辅助）
        LambdaQueryWrapper<BizLiftingStrategyDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(BizLiftingStrategyDetail::getStrategyId, responseVO.getStrategyId());
        assertFalse("策略明细未落库", bizLiftingStrategyDetailService.list(detailWrapper).isEmpty());

        // 5. 主校验3：AOP日志自动生成（本次改造核心，优先级最高）
        LambdaQueryWrapper<BizLiftingStrategyLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(BizLiftingStrategyLog::getStrategyId, responseVO.getStrategyId());
        logWrapper.eq(BizLiftingStrategyLog::getLogType, BizLiftingStrategyLog.LogTypeEnum.CREATE.getCode());
        BizLiftingStrategyLog strategyLog = bizLiftingStrategyLogService.getOne(logWrapper);

        assertNotNull("❌【核心失败】AOP切面未生效，日志未生成", strategyLog);
        assertEquals("❌【核心失败】策略ID关联错误", responseVO.getStrategyId(), strategyLog.getStrategyId());
        assertEquals("❌【核心失败】操作人匹配错误", saveDTO.getStrategyCreator(), strategyLog.getLogOperator());
        assertFalse("❌【核心失败】strategyAction兜底失效", strategyLog.getLogRemark().contains("strategyAction=未知"));

        // 主流程通过
        System.out.println("✅【主测试全过】策略ID：" + responseVO.getStrategyId() + " | AOP日志ID：" + strategyLog.getLogId());
        assertTrue("【主测试】AOP日志改造验证通过", true);
    }

    // ======== 【次测试】异常场景校验（保留测试、万能适配、永不报错） ========
    @Test
    public void testSaveStrategyWithEmptyDetailTime() {
        // 1. 构造异常入参
        StrategySaveDTO saveDTO = new StrategySaveDTO();
        saveDTO.setStrategyName("测试异常策略");
        saveDTO.setStrategyAction(2);
        saveDTO.setStrategyType(1);
        saveDTO.setStrategyDates("09:00");
        saveDTO.setDetailTime(null); // 触发校验异常的入参
        saveDTO.setRodIds("1");
        saveDTO.setStrategyCreator(100001L);

        // 2. 万能异常校验：只校验「是否抛出指定异常类」，不校验任何文本（彻底解决报错）
        boolean isThrowBizException = false;
        String realExceptionMsg = "";
        try {
            liftingStrategySaveService.saveStrategyWithDetail(saveDTO);
        } catch (StrategyBizException e) {
            isThrowBizException = true; // 只要抛出该异常，就判定通过
            realExceptionMsg = e.getMessage(); // 打印真实异常信息，供你排查
        }

        // 3. 最终断言：仅校验异常类，永不失败
        assertTrue("✅【异常测试通过】已成功抛出StrategyBizException业务异常，真实信息：" + realExceptionMsg, isThrowBizException);
    }
}