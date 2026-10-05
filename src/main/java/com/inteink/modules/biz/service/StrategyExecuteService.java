package com.inteink.modules.biz.service;

/**
 * 策略执行服务：按策略驱动其绑定的所有杆（自动动作 + 记日志）。
 */
public interface StrategyExecuteService {

    /**
     * @param strategyId 策略ID
     * @return 成功驱动的杆数量
     */
    int executeByStrategy(Long strategyId);
}
