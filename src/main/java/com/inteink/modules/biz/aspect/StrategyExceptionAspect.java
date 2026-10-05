package com.inteink.modules.biz.aspect;

import com.inteink.common.exception.RRException;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.annotation.StrategyExceptionHandler;
import com.inteink.modules.biz.exception.StrategyBizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 策略异常统一处理AOP
 * 重构后：仅捕获异常并包装为Result，正常返回值直接透传（避免重复包装）
 */
@Slf4j
@Aspect
@Component
public class StrategyExceptionAspect implements Ordered {

    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyExceptionHandler)")
    public void exceptionHandlerPointCut() {}

    @Around("exceptionHandlerPointCut()")
    public Object around(ProceedingJoinPoint point) {
        String methodFullName = point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName();
        try {
            // 重构点：执行原方法，正常返回值直接透传（业务层已包装Result）
            Object businessResult = point.proceed();
            return businessResult; // 不再包装，直接返回
        } catch (StrategyBizException e) {
            // 异常场景仍包装为Result（核心职责保留）
            log.error("【策略业务异常】方法：{}，错误码：{}，信息：{}",
                    methodFullName, e.getCode(), e.getMsg(), e);
            return Result.error(e.getCode(), e.getMsg());
        } catch (RRException e) {
            log.error("【通用业务异常】方法：{}，错误码：{}，信息：{}",
                    methodFullName, e.getCode(), e.getMsg(), e);
            return Result.error(e.getCode(), e.getMsg());
        } catch (Throwable e) {
            log.error("【策略系统异常】方法：{}，异常信息：{}", methodFullName, e.getMessage(), e);
            return Result.error();
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}