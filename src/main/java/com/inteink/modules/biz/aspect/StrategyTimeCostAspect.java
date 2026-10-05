package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.annotation.StrategyTimeCost;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 策略方法耗时统计AOP
 * 适配你已有的@StrategyTimeCost注解（threshold参数），自动统计耗时+慢接口告警
 */
@Slf4j
@Aspect
@Component
public class StrategyTimeCostAspect {

    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyTimeCost)")
    public void timeCostPointCut() {}

    /**
     * @param point
     * @param timeCost
     * @return
     * @throws Throwable
     */
    @Around("timeCostPointCut() && @annotation(timeCost)")
    public Object around(ProceedingJoinPoint point, StrategyTimeCost timeCost) throws Throwable {
        // 1. 获取方法名+配置的慢接口阈值
        String methodName = point.getSignature().getName();
        long slowThreshold = timeCost.threshold(); // 适配你注解中的threshold参数（而非之前的slowThreshold）

        // 2. 记录开始时间
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            // 3. 执行原业务方法
            result = point.proceed();
            return result;
        } finally {
            // 4. 计算耗时+打印日志
            long costTime = System.currentTimeMillis() - startTime;
            if (costTime > slowThreshold) {
                // 慢接口WARN告警
                log.warn("【慢接口告警】策略方法：{}，执行耗时：{}ms，慢接口阈值：{}ms",
                        methodName, costTime, slowThreshold);
            } else {
                // 正常耗时INFO日志
                log.info("【方法耗时统计】策略方法：{}，执行耗时：{}ms", methodName, costTime);
            }
        }
    }
}