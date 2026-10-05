package com.inteink.modules.biz.utils;

import com.inteink.modules.biz.annotation.StrategyTimeCost;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AOP日志格式化工具类
 * 通用格式化入参、出参、方法名，提取注解属性
 */
public class AopLogUtil {

    /**
     * 获取方法全限定名（包名+类名+方法名）
     */
    public static String getMethodFullName(ProceedingJoinPoint point) {
        return point.getTarget().getClass().getName() + "." + point.getSignature().getName();
    }

    /**
     * 入参数组转字符串（格式化）
     */
    public static String parseArgsToString(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        return Arrays.toString(args);
    }

    /**
     * 出参转字符串（格式化）
     */
    public static String parseResultToString(Object result) {
        if (result == null) return "null";
        return result.toString();
    }

    /**
     * 提取@StrategyTimeCost注解的阈值属性
     */
    public static long getAnnotationThreshold(ProceedingJoinPoint point, Class<StrategyTimeCost> annotationClass) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        StrategyTimeCost annotation = method.getAnnotation(annotationClass);
        return annotation == null ? 500 : annotation.threshold();
    }
}