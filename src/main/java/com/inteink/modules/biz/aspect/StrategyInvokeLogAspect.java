package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.annotation.StrategyLogRecord;
import com.inteink.modules.biz.annotation.StrategyTimeCost;
import com.inteink.modules.biz.utils.AopLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * 入参/出参日志 + 方法耗时统计 整合切面
 * 1. @StrategyLogRecord：自动打印入参、出参，生产环境自动关闭入参打印
 * 2. @StrategyTimeCost：自动统计耗时，慢接口（>500ms）打印告警日志
 */
@Slf4j
@Aspect
@Component
public class StrategyInvokeLogAspect {

    @Resource
    private Environment environment;

    // ========== 入参出参日志切入点 ==========
    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyLogRecord)")
    public void logRecordPointCut() {}

    // ========== 耗时统计切入点 ==========
    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyTimeCost)")
    public void timeCostPointCut() {}

    // ========== 入参出参日志拦截 ==========
    @Around("logRecordPointCut()")
    public Object aroundLogRecord(ProceedingJoinPoint point) throws Throwable {
        String methodName = AopLogUtil.getMethodFullName(point);
        // 生产环境关闭入参打印，开发/测试环境开启
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProd = activeProfiles != null && activeProfiles.length > 0 && "prod".equals(activeProfiles[0]);

        // 前置：打印入参（非生产环境）
        if (!isProd) {
            Object[] args = point.getArgs();
            log.info("【策略方法入参】{}，入参：{}", methodName, AopLogUtil.parseArgsToString(args));
        }

        // 执行方法
        Object result = point.proceed();

        // 后置：打印出参（所有环境开启）
        log.info("【策略方法出参】{}，出参：{}", methodName, AopLogUtil.parseResultToString(result));
        return result;
    }

    // ========== 耗时统计拦截 ==========
    @Around("timeCostPointCut()")
    public Object aroundTimeCost(ProceedingJoinPoint point) throws Throwable {
        long threshold = AopLogUtil.getAnnotationThreshold(point, StrategyTimeCost.class);
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long cost = System.currentTimeMillis() - start;

        String methodName = AopLogUtil.getMethodFullName(point);
        if (cost < threshold) {
            log.info("【策略方法耗时】{}，耗时：{}ms", methodName, cost);
        } else {
            log.warn("【策略慢接口告警】{}，耗时：{}ms（阈值：{}ms）", methodName, cost, threshold);
        }
        return result;
    }
}