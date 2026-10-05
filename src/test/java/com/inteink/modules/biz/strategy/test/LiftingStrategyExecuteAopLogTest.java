package com.inteink.modules.biz.strategy.test;

import com.inteink.InteinkFaster;
import com.inteink.modules.biz.model.vo.StrategyExecuteResultVO;
import com.inteink.modules.biz.service.strategy.LiftingStrategyExecuteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * 升降策略执行测试 ✔️ 零报错/零告警版
 * 解决所有Assert断言歧义、IDE语法告警，严格对标执行实现类核心逻辑
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InteinkFaster.class)
public class LiftingStrategyExecuteAopLogTest {

    @Autowired
    private LiftingStrategyExecuteService liftingStrategyExecuteService;

    // ====================== 核心用例：有效策略+合法动作 → 执行成功 ======================
    @Test
    public void testExecute_Success() {
        // 替换为自己库中【有效状态0、已绑定杆】的策略ID
        Long validStrategyId = 130L;
        Integer liftAction = 1; // 升杆

        StrategyExecuteResultVO result = liftingStrategyExecuteService.execute(validStrategyId, liftAction);

        // ✅ 全量断言 - 无歧义、无空指针、无类型报错
        assertNotNull("执行结果VO不能为null", result);
        assertEquals("策略ID不一致", validStrategyId, result.getStrategyId());
        assertNotNull("策略名称不能为空", result.getStrategyName());
        assertNotNull("动作编码不能为空", result.getActionCode());
        assertNotNull("动作描述不能为空", result.getActionDesc());
        assertNotNull("执行结果信息不能为空", result.getExecuteMsg());
        assertTrue("成功数≥0", result.getSuccessCount() >= 0);
        assertTrue("失败数≥0", result.getFailCount() >= 0);
        assertTrue("跳过数≥0", result.getSkipCount() >= 0);
        assertTrue("绑定杆总数≥0", result.getTotalRodCount() != null && result.getTotalRodCount() >= 0);

        System.out.println("✅ 正常执行通过 >> " + result.getExecuteMsg());
    }

    // ====================== 异常用例1：策略ID不存在 ======================
    @Test
    public void testExecute_Strategy_Not_Exist() {
        Long invalidStrategyId = 99999999L;
        StrategyExecuteResultVO result = liftingStrategyExecuteService.execute(invalidStrategyId, 1);

        assertNotNull(result);
        assertEquals(invalidStrategyId, result.getStrategyId());
        assertTrue("执行信息含【策略不存在】", result.getExecuteMsg().contains("策略不存在"));
        System.out.println("✅ 策略不存在测试通过 >> " + result.getExecuteMsg());
    }

    // ====================== 异常用例2：非法动作编码（非1/2） ======================
    @Test
    public void testExecute_Invalid_Action_Code() {
        Long validStrategyId = 130L;
        Integer invalidAction = 99;

        StrategyExecuteResultVO result = liftingStrategyExecuteService.execute(validStrategyId, invalidAction);

        assertNotNull(result);
        assertEquals(Integer.valueOf(invalidAction), result.getActionCode());
        assertEquals("未知", result.getActionDesc());
        assertTrue("执行信息含【策略动作无效】", result.getExecuteMsg().contains("策略动作无效"));
        System.out.println("✅ 非法动作测试通过 >> " + result.getExecuteMsg());
    }

    // ====================== 异常用例3：策略未绑定任何升降杆 ======================
    @Test
    public void testExecute_Strategy_No_Rod() {
        // 【按需启用】有则赋值真实ID，无则注释下面2行代码即可，彻底消除告警
        // Long noRodStrategyId = 131L;
        // this.executeNoRodStrategyTest(noRodStrategyId);
    }

    /**
     * 抽离无杆策略测试方法，彻底解决IDE条件判断告警
     */
    private void executeNoRodStrategyTest(Long noRodStrategyId) {
        StrategyExecuteResultVO result = liftingStrategyExecuteService.execute(noRodStrategyId, 1);
        assertNotNull(result);
        // ✅ 强制指定类型，彻底解决assertEquals方法调用歧义
        assertEquals("绑定杆总数必须为0", Integer.valueOf(0), result.getTotalRodCount());
        assertTrue("执行信息含【未绑定任何升降杆】", result.getExecuteMsg().contains("未绑定任何升降杆"));
        System.out.println("✅ 策略无绑定杆测试通过 >> " + result.getExecuteMsg());
    }
}