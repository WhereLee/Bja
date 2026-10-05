package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.annotation.TimeCost;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 耗时统计切面：环绕 @TimeCost 方法，记录执行耗时，超阈值 warn。
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class TimeCostAspect {

    @Around("@annotation(com.inteink.modules.biz.annotation.TimeCost)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long cost = System.currentTimeMillis() - start;
            TimeCost tc = resolveAnnotation(pjp);
            if (tc != null) {
                String tag = tc.value().isEmpty() ? methodName(pjp) : tc.value();
                if (cost >= tc.warnMs()) {
                    log.warn("【耗时】{} 方法={} 耗时={}ms(阈值{}ms)", tag, methodName(pjp), cost, tc.warnMs());
                } else {
                    log.info("【耗时】{} 方法={} 耗时={}ms", tag, methodName(pjp), cost);
                }
            }
        }
    }

    private TimeCost resolveAnnotation(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        return method.getAnnotation(TimeCost.class);
    }

    private String methodName(ProceedingJoinPoint pjp) {
        return pjp.getSignature().getDeclaringType().getSimpleName()
                + "." + pjp.getSignature().getName();
    }
}
