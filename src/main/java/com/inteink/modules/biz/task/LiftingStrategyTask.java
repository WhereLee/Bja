package com.inteink.modules.biz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inteink.modules.biz.service.StrategyExecuteService;
import com.inteink.modules.job.task.ITask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 策略定时任务体（供 job 模块反射调用 run(params)）。
 * 注册为 Spring bean 名 liftingStrategyTask，与 StrategyScheduleFactory 的 jobBean 前缀一致。
 */
@Slf4j
@Component("liftingStrategyTask")
@RequiredArgsConstructor
public class LiftingStrategyTask implements ITask {

    private final StrategyExecuteService strategyExecuteService;

    @Override
    public void run(String params) {
        try {
            JSONObject json = JSON.parseObject(params);
            Long strategyId = json.getLong("strategyId");
            log.info("【策略定时任务】触发，strategyId={}, params={}", strategyId, params);
            strategyExecuteService.executeByStrategy(strategyId);
        } catch (Exception e) {
            log.error("【策略定时任务】执行异常，params=" + params, e);
        }
    }
}
