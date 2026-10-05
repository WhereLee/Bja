package com.inteink.modules.biz.constant;

/**
 * 策略Redis缓存Key统一管理
 * 遵循项目RedisKeys命名规范，避免缓存Key冲突
 */
public class StrategyRedisKeys {

    /**
     * 策略单对象缓存Key前缀
     * 完整格式：strategy:info:{strategyId}
     */
    public static final String STRATEGY_INFO_KEY = "strategy:info:";

    /**
     * 获取策略缓存完整Key
     */
    public static String getStrategyInfoKey(Long strategyId) {
        return STRATEGY_INFO_KEY + strategyId;
    }
}