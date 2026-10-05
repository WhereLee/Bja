//package com.inteink.modules.biz.test;
//
//import com.inteink.InteinkFaster;
//import com.inteink.modules.biz.dto.StrategyAuditDTO;
//import com.inteink.modules.biz.dto.StrategySaveDTO;
//import com.inteink.modules.biz.entity.BizLiftingStrategy;
//import com.inteink.modules.biz.eums.BizLiftingStrategyEnum;
//import com.inteink.modules.biz.exception.StrategyBizException;
//import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
//import com.inteink.modules.biz.vo.StrategyResponseVO;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.*;
//
///**
// * 升降策略审核测试类（覆盖参数校验、状态变更、定时任务、异常场景）
// * 注：@Transactional 保证测试后数据回滚，不污染数据库
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = InteinkFaster.class)
//@Transactional // 测试完成后自动回滚，避免脏数据
//public class BizLiftingStrategyAuditTest {
//
//    @Autowired
//    private LiftingStrategyService strategyService;
//
//    // ====================== 前置方法：创建待审核策略（供审核测试使用） ======================
//    /**
//     * 辅助方法：创建一条待审核的策略，返回策略ID
//     */
//    private Long createPendingAuditStrategy() {
//        // 1. 构造合法新增入参
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName("待审核测试策略");
//        saveDTO.setStrategyAction(1); // 升杆
//        saveDTO.setStrategyType(1); // 每日执行
//        saveDTO.setStrategyDates("08:00");
//        saveDTO.setDetailTime("08:00,20:00");
//        saveDTO.setRodIds("2"); // 数据库中存在的杆ID
//        saveDTO.setStrategyCreator(2L);
//
//        // 2. 执行新增，返回待审核策略ID
//        StrategyResponseVO saveVO = strategyService.saveStrategyWithDetail(saveDTO);
//        return saveVO.getStrategyId();
//    }
//
//    // ====================== 正常场景测试 ======================
//    /**
//     * 测试1：审核通过（全参数合法）
//     * 验证：审核状态更新为通过、审核人/时间记录、定时任务生成
//     */
//    @Test
//    public void testAuditStrategy_Pass() {
//        System.out.println("=== 测试1：审核策略（通过） ===");
//        // 1. 前置：创建待审核策略
//        Long strategyId = createPendingAuditStrategy();
//        System.out.println("创建待审核策略ID：" + strategyId);
//
//        // 2. 构造审核通过入参
//        StrategyAuditDTO auditDTO = new StrategyAuditDTO();
//        auditDTO.setStrategyId(strategyId);
//        auditDTO.setCheckState(1); // 1=审核通过（枚举值）
//        auditDTO.setOperatorId(1002L); // 审核人ID
//
//        // 3. 执行审核
//        StrategyResponseVO auditVO = strategyService.auditStrategy(auditDTO);
//
//        // 4. 断言验证
//        // 4.1 返回VO非空，策略ID匹配
//        assertNotNull("审核返回VO为空", auditVO);
//        assertEquals("策略ID不匹配", strategyId, auditVO.getStrategyId());
//        System.out.println("审核通过，返回信息：" + auditVO.getMsg());
//
//        // 4.2 审核状态更新为通过（1）
//        BizLiftingStrategy auditedStrategy = strategyService.getById(strategyId);
//        assertEquals("审核状态未更新为通过", 1, auditedStrategy.getStrategyCheckState().intValue());
//        assertEquals("审核人ID错误", 1002L, auditedStrategy.getStrategyCheckUser().longValue());
//        assertNotNull("审核时间未记录", auditedStrategy.getStrategyCheckTime());
//        assertNotNull("策略更新时间未刷新", auditedStrategy.getStrategyUpdatetime());
//
//        // 4.3 返回信息验证（包含定时任务生成提示）
//        assertTrue("审核通过提示信息错误", auditVO.getMsg().contains("审核通过") && auditVO.getMsg().contains("定时执行任务"));
//
//        System.out.println("✅ 审核通过测试通过");
//    }
//
//    /**
//     * 测试2：审核驳回（全参数合法）
//     * 验证：审核状态更新为驳回、审核人/时间记录、无定时任务生成
//     */
//    @Test
//    public void testAuditStrategy_Reject() {
//        System.out.println("=== 测试2：审核策略（驳回） ===");
//        // 1. 前置：创建待审核策略
//        Long strategyId = createPendingAuditStrategy();
//        System.out.println("创建待审核策略ID：" + strategyId);
//
//        // 2. 构造审核驳回入参
//        StrategyAuditDTO auditDTO = new StrategyAuditDTO();
//        auditDTO.setStrategyId(strategyId);
//        auditDTO.setCheckState(2); // 2=审核驳回（枚举值）
//        auditDTO.setOperatorId(1002L); // 审核人ID
//
//        // 3. 执行审核
//        StrategyResponseVO auditVO = strategyService.auditStrategy(auditDTO);
//
//        // 4. 断言验证
//        // 4.1 返回VO非空，策略ID匹配
//        assertNotNull("审核返回VO为空", auditVO);
//        assertEquals("策略ID不匹配", strategyId, auditVO.getStrategyId());
//        System.out.println("审核驳回，返回信息：" + auditVO.getMsg());
//
//        // 4.2 审核状态更新为驳回（2）
//        BizLiftingStrategy auditedStrategy = strategyService.getById(strategyId);
//        assertEquals("审核状态未更新为驳回", 2, auditedStrategy.getStrategyCheckState().intValue());
//        assertEquals("审核人ID错误", 1002L, auditedStrategy.getStrategyCheckUser().longValue());
//        assertNotNull("审核时间未记录", auditedStrategy.getStrategyCheckTime());
//
//        // 4.3 返回信息验证（无定时任务生成）
//        assertTrue("审核驳回提示信息错误", auditVO.getMsg().contains("审核驳回") && auditVO.getMsg().contains("未生成定时执行任务"));
//
//        System.out.println("✅ 审核驳回测试通过");
//    }
//
//    // ====================== 边界场景测试 ======================
//    /**
//     * 测试3：审核已通过的策略（重复审核）
//     * 验证：抛出重复审核的业务异常
//     */
//    @Test
//    public void testAuditStrategy_DuplicatePass() {
//        System.out.println("=== 测试3：审核策略（重复审核通过） ===");
//        // 1. 前置：创建待审核策略并先审核通过
//        Long strategyId = createPendingAuditStrategy();
//        StrategyAuditDTO firstAuditDTO = new StrategyAuditDTO();
//        firstAuditDTO.setStrategyId(strategyId);
//        firstAuditDTO.setCheckState(1);
//        firstAuditDTO.setOperatorId(1002L);
//        strategyService.auditStrategy(firstAuditDTO);
//        System.out.println("首次审核通过策略ID：" + strategyId);
//
//        // 2. 构造重复审核通过入参
//        StrategyAuditDTO duplicateAuditDTO = new StrategyAuditDTO();
//        duplicateAuditDTO.setStrategyId(strategyId);
//        duplicateAuditDTO.setCheckState(1);
//        duplicateAuditDTO.setOperatorId(1002L);
//
//        // 3. 执行重复审核，断言抛出异常
//        try {
//            strategyService.auditStrategy(duplicateAuditDTO);
//            fail("未抛出重复审核的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("已通过") && e.getMessage().contains("不允许重复审核"));
//        }
//    }
//
//    /**
//     * 测试4：审核已驳回的策略（重复审核）
//     * 验证：抛出重复审核的业务异常
//     */
//    @Test
//    public void testAuditStrategy_DuplicateReject() {
//        System.out.println("=== 测试4：审核策略（重复审核驳回） ===");
//        // 1. 前置：创建待审核策略并先审核驳回
//        Long strategyId = createPendingAuditStrategy();
//        StrategyAuditDTO firstAuditDTO = new StrategyAuditDTO();
//        firstAuditDTO.setStrategyId(strategyId);
//        firstAuditDTO.setCheckState(2);
//        firstAuditDTO.setOperatorId(1002L);
//        strategyService.auditStrategy(firstAuditDTO);
//        System.out.println("首次审核驳回策略ID：" + strategyId);
//
//        // 2. 构造重复审核驳回入参
//        StrategyAuditDTO duplicateAuditDTO = new StrategyAuditDTO();
//        duplicateAuditDTO.setStrategyId(strategyId);
//        duplicateAuditDTO.setCheckState(2);
//        duplicateAuditDTO.setOperatorId(1002L);
//
//        // 3. 执行重复审核，断言抛出异常
//        try {
//            strategyService.auditStrategy(duplicateAuditDTO);
//            fail("未抛出重复审核的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("已驳回") && e.getMessage().contains("不允许重复审核"));
//        }
//    }
//
//    // ====================== 异常场景测试 ======================
//    /**
//     * 测试5：审核不存在的策略
//     * 验证：抛出策略不存在的业务异常
//     */
//    @Test
//    public void testAuditStrategy_NotFound() {
//        System.out.println("=== 测试5：审核策略（策略不存在） ===");
//        // 1. 构造入参（策略ID=999999，不存在）
//        StrategyAuditDTO auditDTO = new StrategyAuditDTO();
//        auditDTO.setStrategyId(999999L);
//        auditDTO.setCheckState(1);
//        auditDTO.setOperatorId(1002L);
//
//        // 2. 执行审核，断言抛出异常
//        try {
//            strategyService.auditStrategy(auditDTO);
//            fail("未抛出策略不存在的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("策略不存在"));
//        }
//    }
//
//    /**
//     * 测试6：审核已逻辑删除的策略
//     * 验证：抛出策略已删除的业务异常
//     */
//    @Test
//    public void testAuditStrategy_Deleted() {
//        System.out.println("=== 测试6：审核策略（策略已删除） ===");
//        // 1. 前置：创建待审核策略并标记为删除
//        Long strategyId = createPendingAuditStrategy();
//        BizLiftingStrategy strategy = strategyService.getById(strategyId);
//        strategy.setStrategyStatus((Long) BizLiftingStrategyEnum.STRATEGY_STATUS_DELETED.getCode()); // 1L=逻辑删除
//        strategyService.updateById(strategy);
//        System.out.println("标记为删除的策略ID：" + strategyId);
//
//        // 2. 构造审核入参
//        StrategyAuditDTO auditDTO = new StrategyAuditDTO();
//        auditDTO.setStrategyId(strategyId);
//        auditDTO.setCheckState(1);
//        auditDTO.setOperatorId(1002L);
//
//        // 3. 执行审核，断言抛出异常
//        try {
//            strategyService.auditStrategy(auditDTO);
//            fail("未抛出策略已删除的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("已逻辑删除") && e.getMessage().contains("不允许审核"));
//        }
//    }
//
//    /**
//     * 测试7：审核状态非法（非1/2）
//     * 验证：抛出参数非法的业务异常
//     */
//    @Test
//    public void testAuditStrategy_InvalidCheckState() {
//        System.out.println("=== 测试7：审核策略（审核状态非法） ===");
//        // 1. 前置：创建待审核策略
//        Long strategyId = createPendingAuditStrategy();
//
//        // 2. 构造入参（审核状态=99，非法值）
//        StrategyAuditDTO auditDTO = new StrategyAuditDTO();
//        auditDTO.setStrategyId(strategyId);
//        auditDTO.setCheckState(99); // 非法状态
//        auditDTO.setOperatorId(1002L);
//
//        // 3. 执行审核，断言抛出异常
//        try {
//            strategyService.auditStrategy(auditDTO);
//            fail("未抛出审核状态非法的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("审核状态只能是1（通过）或2（驳回）"));
//        }
//    }
//
//    /**
//     * 测试8：审核参数为空（策略ID/审核状态/审核人ID为空）
//     * 验证：抛出参数为空的业务异常
//     */
//    /**
//     * 测试8：审核参数为空（策略ID/审核状态/审核人ID为空）
//     * 验证：抛出参数为空的业务异常
//     */
//    @Test
//    public void testAuditStrategy_EmptyParam() {
//        System.out.println("=== 测试8：审核策略（参数为空） ===");
//        // 分3个子场景测试（避免多参数为空时异常信息不唯一）
//        // 子场景1：策略ID为空
//        StrategyAuditDTO dto1 = new StrategyAuditDTO();
//        dto1.setStrategyId(null);
//        dto1.setCheckState(1);
//        dto1.setOperatorId(1002L);
//        try {
//            strategyService.auditStrategy(dto1);
//            fail("未抛出策略ID为空的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获策略ID为空异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("策略ID不能为空"));
//        }
//
//        // 子场景2：审核状态为空
//        StrategyAuditDTO dto2 = new StrategyAuditDTO();
//        dto2.setStrategyId(1L);
//        dto2.setCheckState(null);
//        dto2.setOperatorId(1002L);
//        try {
//            strategyService.auditStrategy(dto2);
//            fail("未抛出审核状态为空的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获审核状态为空异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("审核状态不能为空"));
//        }
//
//        // 子场景3：审核人ID为空
//        StrategyAuditDTO dto3 = new StrategyAuditDTO();
//        dto3.setStrategyId(1L);
//        dto3.setCheckState(1);
//        dto3.setOperatorId(null);
//        try {
//            strategyService.auditStrategy(dto3);
//            fail("未抛出审核人ID为空的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获审核人ID为空异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("审核人ID不能为空"));
//        }
//    }
//}