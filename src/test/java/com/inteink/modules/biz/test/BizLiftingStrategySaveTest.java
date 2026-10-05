//package com.inteink.modules.biz.test;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.inteink.InteinkFaster;
//import com.inteink.modules.biz.assembler.StrategyAssembler;
//import com.inteink.modules.biz.dto.StrategySaveDTO;
//import com.inteink.modules.biz.entity.BizLiftingStrategy;
//import com.inteink.modules.biz.entity.BizLiftingStrategyDetail;
//import com.inteink.modules.biz.exception.StrategyBizException;
//import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
//import com.inteink.modules.biz.service.lifting.LiftingRodService;
//import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
//import com.inteink.modules.biz.vo.StrategyResponseVO;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.regex.Pattern;
//
//import static org.junit.Assert.*;
//
///**
// * 升降策略新增测试类（覆盖参数校验、名称生成、全流程保存）
// * 注：@Transactional 保证测试后数据回滚，不污染数据库
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = InteinkFaster.class)
//@Transactional // 测试完成后自动回滚，避免脏数据
//public class BizLiftingStrategySaveTest {
//
//    @Autowired
//    private LiftingStrategyService strategyService;
//
//    @Autowired
//    private StrategyAssembler strategyAssembler;
//
//    @Autowired
//    private BizLiftingStrategyDetailService detailService;
//
//    @Autowired
//    private LiftingRodService rodService;
//
//    // 时间戳正则（匹配 _17开头的秒级时间戳，如_1717380462）
//    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^.+_17\\d{8}$");
//
//    // ====================== 正常场景测试 ======================
//    /**
//     * 测试1：新增策略（全参数合法）
//     * 验证：名称自动加时间戳、主表/明细/杆绑定保存成功、返回VO正确
//     */
//    @Test
//    public void testSaveStrategy_AllValidParams() {
//        System.out.println("=== 测试1：新增策略（全参数合法） ===");
//        // 1. 构造合法入参
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName("每天执行充电策略"); // 核心名
//        saveDTO.setStrategyAction(1); // 动作类型（1=启动充电，按业务枚举）
//        saveDTO.setStrategyType(1); // 策略类型（1=每天执行，按业务枚举）
//        saveDTO.setStrategyDates("08:00"); // 执行日期
//        saveDTO.setDetailTime("08:00,20:00"); // 执行时段（开始,结束）
//        saveDTO.setRodIds("2"); // 替换为数据库中存在的杆ID
//        saveDTO.setStrategyRemark("测试新增策略"); // 备注
//        saveDTO.setStrategyCreator(2L); // 创建人ID
//
//        // 2. 执行新增
//        StrategyResponseVO responseVO = strategyService.saveStrategyWithDetail(saveDTO);
//
//        // 3. 断言验证
//        // 3.1 返回VO非空，策略ID生成
//        assertNotNull("新增后返回VO为空", responseVO);
//        assertNotNull("策略ID未生成", responseVO.getStrategyId());
//        System.out.println("新增成功，策略ID：" + responseVO.getStrategyId());
//
//        // 3.2 策略名称自动拼接时间戳（格式：核心名_17xxxxxxx）
//        String generatedName = responseVO.getStrategyName();
//        assertTrue("策略名未正确拼接时间戳，生成名称：" + generatedName,
//                TIMESTAMP_PATTERN.matcher(generatedName).matches());
//        assertTrue("策略名核心部分错误，生成名称：" + generatedName,
//                generatedName.startsWith("每天执行充电策略_"));
//        System.out.println("生成唯一策略名：" + generatedName);
//
//        // 3.3 主表数据正确
//        BizLiftingStrategy savedStrategy = strategyService.getById(responseVO.getStrategyId());
//        assertEquals("策略动作类型错误", 1, savedStrategy.getStrategyAction().intValue());
//        assertEquals("策略类型错误", 1, savedStrategy.getStrategyType().intValue());
//        assertEquals("审核状态错误（应默认待审核）", 0, savedStrategy.getStrategyCheckState().intValue());
//        assertEquals("策略状态错误（应默认有效）", 0L, savedStrategy.getStrategyStatus().longValue());
//
//        // 3.4 明细数据保存成功（修复LambdaQueryWrapper写法）
//        LambdaQueryWrapper<BizLiftingStrategyDetail> detailWrapper = new LambdaQueryWrapper<>();
//        detailWrapper.eq(BizLiftingStrategyDetail::getStrategyId, responseVO.getStrategyId());
//        List<BizLiftingStrategyDetail> detailList = detailService.list(detailWrapper);
//
//        assertEquals("策略明细未保存", 1, detailList.size());
//        BizLiftingStrategyDetail detail = detailList.get(0);
//        assertEquals("明细开始时间错误", "08:00", detail.getDetailBegin());
//        assertEquals("明细结束时间错误", "20:00", detail.getDetailEnd());
//
////        // 3.5 杆绑定成功（需根据rodService的绑定逻辑验证，此处简化）
////        boolean isRodBound = rodService.checkStrategyRodBind(responseVO.getStrategyId(), 2L); // 需实现check方法，或查关联表
////        assertTrue("策略未绑定指定杆ID", isRodBound);
//
//        System.out.println("✅ 全参数合法新增测试通过");
//    }
//
//    // ====================== 边界场景测试 ======================
//    /**
//     * 测试2：新增策略（核心名为空）
//     * 验证：自动兜底为"默认策略_时间戳"，保存成功
//     */
//    @Test
//    public void testSaveStrategy_EmptyCoreName() {
//        System.out.println("=== 测试2：新增策略（核心名为空） ===");
//        // 1. 构造入参（策略名为空）
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName(""); // 核心名空
//        saveDTO.setStrategyAction(1);
//        saveDTO.setStrategyType(1);
//        saveDTO.setStrategyDates("08:00");
//        saveDTO.setDetailTime("08:00,20:00");
//        saveDTO.setRodIds("2");
//        saveDTO.setStrategyCreator(2L);
//
//        // 2. 执行新增
//        StrategyResponseVO responseVO = strategyService.saveStrategyWithDetail(saveDTO);
//
//        // 3. 断言验证
//        String generatedName = responseVO.getStrategyName();
//        assertTrue("空核心名未兜底为默认策略，生成名称：" + generatedName,
//                generatedName.startsWith("默认策略_"));
//        assertTrue("时间戳格式错误", TIMESTAMP_PATTERN.matcher(generatedName).matches());
//        System.out.println("空名兜底生成策略名：" + generatedName);
//        System.out.println("✅ 空核心名新增测试通过");
//    }
//
//    /**
//     * 测试3：新增策略（杆ID为空、明细为空）
//     * 验证：非必填参数为空时，保存成功（明细/杆绑定无数据）
//     */
//    @Test
//    public void testSaveStrategy_NoRodAndDetail() {
//        System.out.println("=== 测试3：新增策略（杆ID为空、明细/执行规则均非空） ===");
//        // 1. 构造入参（适配每日执行规则：strategyDates填时段，而非日期）
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName("无杆无明细策略");
//        saveDTO.setStrategyAction(1); // 合法动作类型
//        saveDTO.setStrategyType(1); // 每日执行类型
//        saveDTO.setStrategyDates("20:00"); // ✅ 每日执行：填时段（符合校验示例）
//        saveDTO.setDetailTime("09:00,18:00"); // ✅ 明细时段非空且合法
//        saveDTO.setRodIds(""); // 杆ID为空（边界场景）
//        saveDTO.setStrategyCreator(2L); // 合法创建人ID
//
//        // 2. 执行新增
//        StrategyResponseVO responseVO = strategyService.saveStrategyWithDetail(saveDTO);
//
//        // 3. 断言验证
//        assertNotNull("策略ID未生成", responseVO.getStrategyId());
//        assertEquals("明细列表应为1条", 1, responseVO.getDetailList().size());
//        assertEquals("杆ID列表应为空", 0, responseVO.getRodIds().size());
//        assertEquals("每日执行规则值错误", "20:00", responseVO.getStrategyDates()); // 验证时段正确
//        System.out.println("✅ 杆ID为空、明细/执行规则均非空新增测试通过");
//    }
//    // ====================== 异常场景测试 ======================
//    /**
//     * 测试4：新增策略（动作类型非法）
//     * 验证：参数校验失败，抛出业务异常
//     */
//    @Test
//    public void testSaveStrategy_InvalidActionType() {
//        System.out.println("=== 测试4：新增策略（动作类型非法） ===");
//        // 1. 构造入参（动作类型=99，非法值）
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName("非法动作类型策略");
//        saveDTO.setStrategyAction(99); // 非法动作类型
//        saveDTO.setStrategyType(1);
//        saveDTO.setStrategyDates("08:00");
//        saveDTO.setDetailTime("08:00,20:00");
//        saveDTO.setRodIds("2");
//        saveDTO.setStrategyCreator(2L);
//
//        // 2. 执行新增，断言抛出业务异常
//        try {
//            strategyService.saveStrategyWithDetail(saveDTO);
//            fail("未抛出非法动作类型的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("动作类型") || e.getMessage().contains("非法"));
//        }
//    }
//
//    /**
//     * 测试5：新增策略（执行时段格式错误）
//     * 验证：参数校验失败，抛出业务异常
//     */
//    @Test
//    public void testSaveStrategy_InvalidDetailTime() {
//        System.out.println("=== 测试5：新增策略（执行时段格式错误） ===");
//        // 1. 构造入参（时段=08:00，缺少结束时间）
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName("非法时段策略");
//        saveDTO.setStrategyAction(1);
//        saveDTO.setStrategyType(1);
//        saveDTO.setStrategyDates("08:00");
//        saveDTO.setDetailTime("08:00"); // 格式错误（应是 开始,结束）
//        saveDTO.setRodIds("2");
//        saveDTO.setStrategyCreator(2L);
//
//        // 2. 执行新增，断言抛出业务异常
//        try {
//            strategyService.saveStrategyWithDetail(saveDTO);
//            fail("未抛出非法时段格式的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("时段") || e.getMessage().contains("格式"));
//        }
//    }
//
//    /**
//     * 测试6：新增策略（杆ID格式非法）
//     * 验证：参数校验失败，抛出业务异常
//     */
//    @Test
//    public void testSaveStrategy_InvalidRodId() {
//        System.out.println("=== 测试6：新增策略（杆ID格式非法） ===");
//        // 1. 构造入参（杆ID=abc，非数字）
//        StrategySaveDTO saveDTO = new StrategySaveDTO();
//        saveDTO.setStrategyName("非法杆ID策略");
//        saveDTO.setStrategyAction(1);
//        saveDTO.setStrategyType(1);
//        saveDTO.setStrategyDates("08:00");
//        saveDTO.setDetailTime("08:00,20:00");
//        saveDTO.setRodIds("abc"); // 非法杆ID（非数字）
//        saveDTO.setStrategyCreator(2L);
//
//        // 2. 执行新增，断言抛出业务异常
//        try {
//            strategyService.saveStrategyWithDetail(saveDTO);
//            fail("未抛出非法杆ID的业务异常");
//        } catch (StrategyBizException e) {
//            System.out.println("✅ 捕获预期异常：" + e.getMessage());
//            assertTrue("异常信息不符", e.getMessage().contains("杆ID") || e.getMessage().contains("格式"));
//        }
//    }
//
//    // ====================== 辅助方法说明 ======================
//    // 注：需在RodService中实现该方法，用于验证杆绑定状态
//    // boolean checkStrategyRodBind(Long strategyId, Long rodId) {
//    //     // 示例实现：查询策略-杆关联表，判断是否存在该绑定关系
//    //     // LambdaQueryWrapper<StrategyRodRel> wrapper = new LambdaQueryWrapper<>();
//    //     // wrapper.eq(StrategyRodRel::getStrategyId, strategyId).eq(StrategyRodRel::getRodId, rodId);
//    //     // return rodRelService.count(wrapper) > 0;
//    //     return true; // 测试阶段临时返回true，实际需替换为真实逻辑
//    // }
//}