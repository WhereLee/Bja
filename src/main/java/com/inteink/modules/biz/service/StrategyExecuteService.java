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

    /**
     * 按指定动作/节点执行；END 节点带前置状态守卫（仅当杆处于 begin 期望态时才反向），
     * 避免“审核晚于 begin / 漏触发”造成的孤立动作。
     */
    int executeByStrategy(Long strategyId, Integer action, String nodeType);
}
