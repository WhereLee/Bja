package com.inteink.modules.biz.test;

import com.alibaba.fastjson.JSONObject;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
import com.inteink.modules.biz.task.LiftingStrategyTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 升降杆定时任务测试类（直接运行，无需启动定时框架）
 * 运行前：修改 TEST_STRATEGY_ID 和 TEST_ACTION 为你的测试数据
 */
@Slf4j
@SpringBootTest // 启动Spring容器，加载所有Bean
public class LiftingStrategyTaskTest {

    // ========== 配置测试数据（修改这里！）==========
    private static final Long TEST_STRATEGY_ID = 57L; // 你的策略ID
    private static final Integer TEST_ACTION = 2; // 1=升杆，2=降杆

    // ========== 注入需要测试的核心组件 ==========
    @Autowired
    private LiftingStrategyTask liftingStrategyTask; // 待测试的Task
    @Autowired
    private LiftingStrategyService liftingStrategyService; // 策略服务
    @Autowired
    private BizLiftingStrategyRodService strategyRodService; // 杆关联服务

    /**
     * 测试1：基础验证（策略/杆数据是否存在）
     */
    @Test
    public void testBaseData() {
        log.info("======= 开始基础数据验证 =======");

        // 1. 验证策略是否存在且有效
        BizLiftingStrategy strategy = liftingStrategyService.getById(TEST_STRATEGY_ID);
        if (strategy == null) {
            log.error("❌ 策略不存在！strategyId={}", TEST_STRATEGY_ID);
            return;
        }
        log.info("✅ 策略存在：strategyId={}, 名称={}, 状态={}",
                TEST_STRATEGY_ID, strategy.getStrategyName(), strategy.getStrategyStatus());

        // 2. 验证策略状态是否有效（0=有效）
        if (strategy.getStrategyStatus() == null || strategy.getStrategyStatus() != 0L) {
            log.error("❌ 策略已失效！strategyStatus={}", strategy.getStrategyStatus());
            return;
        }
        log.info("✅ 策略状态有效");

        // 3. 验证杆关联数据是否存在
        List<Long> rodIds = strategyRodService.getRodIdsByStrategyId(TEST_STRATEGY_ID);
        if (CollectionUtils.isEmpty(rodIds)) {
            log.error("❌ 策略未绑定任何升降杆！");
            return;
        }
        log.info("✅ 策略绑定的杆ID列表：{}", rodIds);

        log.info("======= 基础数据验证通过 =======");
    }

    /**
     * 测试2：模拟定时任务调用Task（核心测试）
     */
    @Test
    public void testTaskRun() {
        log.info("======= 开始模拟定时任务调用 =======");

        // 1. 构造与定时任务完全一致的参数
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("strategyId", TEST_STRATEGY_ID);
        paramsJson.put("action", TEST_ACTION);
        String params = paramsJson.toJSONString();
        log.info("模拟定时任务参数：{}", params);

        // 2. 直接调用Task的run方法（绕过定时框架）
        try {
            liftingStrategyTask.run(params);
            log.info("✅ Task执行完成！");
        } catch (Exception e) {
            log.error("❌ Task执行异常！", e);
        }

        log.info("======= 模拟定时任务调用结束 =======");
    }

    /**
     * 测试3：极简测试（仅打印参数/杆ID，排除业务逻辑干扰）
     */
    @Test
    public void testSimpleTask() {
        log.info("======= 开始极简Task测试 =======");

        String params = "{\"strategyId\":\"" + TEST_STRATEGY_ID + "\",\"action\":\"" + TEST_ACTION + "\"}";
        log.info("极简测试参数：{}", params);

        try {
            JSONObject paramJson = JSONObject.parseObject(params);
            Long strategyId = paramJson.getLong("strategyId");
            List<Long> rodIds = strategyRodService.getRodIdsByStrategyId(strategyId);

            log.info("✅ 极简测试成功：strategyId={}, 杆ID列表={}", strategyId, rodIds);
        } catch (Exception e) {
            log.error("❌ 极简测试失败", e);
        }

        log.info("======= 极简Task测试结束 =======");
    }
}