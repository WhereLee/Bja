//package com.inteink.modules.biz.test;
//
//import com.inteink.modules.biz.dto.StrategyAuditDTO;
//import com.inteink.modules.biz.dto.StrategySaveDTO;
//import com.inteink.modules.biz.dto.StrategyUpdateDTO;
//import com.inteink.modules.biz.eums.BizLiftingStrategyEnum;
//import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
//import com.inteink.modules.biz.vo.StrategyResponseVO;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * 升降策略全流程测试：新增 → 审核驳回 → 修改 → 审核通过
// * 注：禁用事务回滚，数据会留存数据库，可直接查询验证
// */
//@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional(rollbackFor = Exception.class)
//@Rollback(false) // 核心：禁用回滚，数据留存
//public class LiftingStrategyFullFlowTest {
//
//    @Autowired
//    private LiftingStrategyService liftingStrategyService;
//
//    // 测试用固定参数（可根据实际业务调整）
//    private static final Long OPERATOR_ID = 1001L; // 操作人ID（需存在于你的用户表）
//    private static final Long ROD_ID = 1L; // 升降杆ID（需存在于你的杆表）
//    private Long strategyId; // 流程中复用的策略ID
//
//    /**
//     * 步骤1：新增策略
//     */
//    @Test
//    public void step1_saveStrategy() {
//        // 1. 构造新增参数
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName("测试全流程策略_初始");
//        saveDTO.setStrategyAction(2); // 2=降杆
//        saveDTO.setStrategyType(1); // 1=每日执行
//        saveDTO.setStrategyDates("21:00"); // 每日21点执行
//        saveDTO.setDetailTime("21:00,06:00"); // 执行时段
//        saveDTO.setRodIds(ROD_ID.toString()); // 关联升降杆ID
//        saveDTO.setStrategyRemark("全流程测试-初始策略");
//        saveDTO.setStrategyCreator(OPERATOR_ID); // 创建人
//
//        // 2. 调用新增接口
//        StrategyResponseVO response = liftingStrategyService.saveStrategyWithDetail(saveDTO);
//        strategyId = response.getStrategyId(); // 保存策略ID，供后续步骤使用
//
//        // 3. 断言验证
//        assert response != null : "新增策略返回空";
//        assert strategyId != null : "新增策略ID为空";
//        assert "待审核".equals(response.getAuditStatus()) : "新增策略默认状态非待审核";
//        log.info("【步骤1-新增策略】成功，策略ID：{}，名称：{}", strategyId, response.getStrategyName());
//    }
//
//    /**
//     * 步骤2：审核驳回
//     * 依赖：步骤1执行成功，strategyId已赋值
//     */
//    @Test
//    public void step2_auditReject() {
//        // 1. 构造审核参数（驳回）
//        StrategyAuditDTO auditDTO = new StrategyAuditDTO();
//        auditDTO.setStrategyId(strategyId);
//        auditDTO.setCheckState((Integer) BizLiftingStrategyEnum.CHECK_STATE_REJECT.getCode()); // 2=驳回
//        auditDTO.setOperatorId(OPERATOR_ID); // 审核人
//
//        // 2. 调用审核接口
//        StrategyResponseVO response = liftingStrategyService.auditStrategy(auditDTO);
//
//        // 3. 断言验证
//        assert response != null : "审核驳回返回空";
//        assert "审核驳回".equals(response.getAuditStatus()) : "审核状态未更新为驳回";
//        log.info("【步骤2-审核驳回】成功，策略ID：{}，审核状态：{}", strategyId, response.getAuditStatus());
//    }
//
//    /**
//     * 步骤3：修改策略
//     * 依赖：步骤2执行成功，strategyId已赋值
//     */
//    @Test
//    public void step3_updateStrategy() {
//        // 1. 构造修改参数
//        StrategyUpdateDTO updateDTO = new StrategyUpdateDTO();
//        updateDTO.setStrategyId(strategyId);
//        updateDTO.setStrategyName("测试全流程策略_修改后"); // 修改名称
//        updateDTO.setStrategyAction(1); // 改为升杆
//        updateDTO.setStrategyType(1); // 保留每日执行
//        updateDTO.setStrategyDates("06:00"); // 改为每日6点执行
//        updateDTO.setDetailTime("06:00,21:00"); // 修改执行时段
//        updateDTO.setRodIds(ROD_ID.toString()); // 保留关联杆ID
//        updateDTO.setStrategyRemark("全流程测试-修改后策略");
//        updateDTO.setStrategyUpdater(OPERATOR_ID); // 修改人
//
//        // 2. 调用修改接口
//        StrategyResponseVO response = liftingStrategyService.updateStrategyWithDetail(updateDTO);
//
//        // 3. 断言验证
//        assert response != null : "修改策略返回空";
//        assert "待审核".equals(response.getAuditStatus()) : "修改后未重置为待审核";
//        assert "测试全流程策略_修改后".contains(response.getStrategyName()) : "策略名称未修改"; // 名称带时间戳，用contains
//        log.info("【步骤3-修改策略】成功，策略ID：{}，新名称：{}", strategyId, response.getStrategyName());
//    }
//
//    /**
//     * 步骤4：审核通过（自动生成定时任务）
//     * 依赖：步骤3执行成功，strategyId已赋值
//     */
//    @Test
//    public void step4_auditPass() {
//        // 1. 构造审核参数（通过）
//        StrategyAuditDTO auditDTO = new StrategyAuditDTO();
//        auditDTO.setStrategyId(strategyId);
//        auditDTO.setCheckState((Integer) BizLiftingStrategyEnum.CHECK_STATE_PASS.getCode()); // 1=通过
//        auditDTO.setOperatorId(OPERATOR_ID); // 审核人
//
//        // 2. 调用审核接口
//        StrategyResponseVO response = liftingStrategyService.auditStrategy(auditDTO);
//
//        // 3. 断言验证
//        assert response != null : "审核通过返回空";
//        assert "审核通过".equals(response.getAuditStatus()) : "审核状态未更新为通过";
//        assert response.getMsg().contains("已生成定时执行任务") : "审核通过未生成定时任务";
//        log.info("【步骤4-审核通过】成功，策略ID：{}，审核状态：{}，提示：{}",
//                strategyId, response.getAuditStatus(), response.getMsg());
//    }
//}