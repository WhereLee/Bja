package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.annotation.BizLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aspectj.lang.JoinPoint;

/**
 * 传给 handler 的上下文：入参(经 joinPoint)、返回值、异常、耗时、操作人。
 */
@Getter
@AllArgsConstructor
public class BizLogContext {

    private final JoinPoint joinPoint;
    private final BizLog annotation;
    private final Object result;
    private final Throwable error;
    private final long costMs;
    /** 操作人；无登录主体时为系统 */
    private final Long operator;

    public boolean success() {
        return error == null;
    }
}
