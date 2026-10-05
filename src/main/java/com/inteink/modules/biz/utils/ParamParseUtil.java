package com.inteink.modules.biz.utils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * AOP参数解析工具类
 * 通用提取方法入参中的「策略ID、操作人ID」，适配所有入参格式
 */
public class ParamParseUtil {

    /**
     * 从入参数组中提取策略ID（适配Long/各种DTO）
     */
    public static Long extractStrategyId(Object[] args) {
        if (args == null || args.length == 0) return null;
        for (Object arg : args) {
            if (arg == null) continue;
            // 入参直接是Long类型的策略ID
            if (arg instanceof Long) {
                return (Long) arg;
            }
            // 从DTO中反射提取strategyId字段
            try {
                Field field = arg.getClass().getDeclaredField("strategyId");
                field.setAccessible(true);
                Object value = field.get(arg);
                if (value instanceof Long) {
                    return (Long) value;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    /**
     * 从入参数组中提取操作人ID
     */
    public static Long extractOperatorId(Object[] args) {
        if (args == null || args.length == 0) return null;
        for (Object arg : args) {
            if (arg == null) continue;
            // 入参直接是Long类型的操作人ID
            if (arg instanceof Long && !Objects.equals(extractStrategyId(args), arg)) {
                return (Long) arg;
            }
            // 从DTO中反射提取operatorId/creator/updater字段
            try {
                Field[] fields = new Field[]{
                        arg.getClass().getDeclaredField("operatorId"),
                        arg.getClass().getDeclaredField("strategyCreator"),
                        arg.getClass().getDeclaredField("strategyUpdater")
                };
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(arg);
                    if (value instanceof Long) {
                        return (Long) value;
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        return 0L;
    }
}