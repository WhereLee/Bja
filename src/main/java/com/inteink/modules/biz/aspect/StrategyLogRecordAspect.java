package com.inteink.modules.biz.aspect;

import com.google.gson.Gson;
import com.inteink.modules.biz.annotation.StrategyLogRecord;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 策略方法入参出参日志AOP
 * 适配你已有的@StrategyLogRecord注解，自动JSON格式化打印入参出参，支持生产环境开关
 */
@Slf4j
@Aspect
@Component
public class StrategyLogRecordAspect {

    // JSON序列化工具（统一格式）
    private static final Gson GSON = new Gson();

    // 读取环境配置（prod环境默认关闭日志打印）
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyLogRecord)")
    public void logRecordPointCut() {}

    @Around("logRecordPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String methodName = point.getSignature().getName();

        // 生产环境直接执行方法，不打印入参出参（避免刷屏）
        if ("prod".equals(activeProfile)) {
            return point.proceed();
        }

        // 1. 打印入参（JSON格式化）
        Object[] args = point.getArgs();
        log.info("【策略方法入参】方法名：{}，入参：{}", methodName, GSON.toJson(args));

        // 2. 执行原业务方法
        Object result = point.proceed();

        // 3. 打印出参（JSON格式化）
        log.info("【策略方法出参】方法名：{}，出参：{}", methodName, GSON.toJson(result));

        return result;
    }
}