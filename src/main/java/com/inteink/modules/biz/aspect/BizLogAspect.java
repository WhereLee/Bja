package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.annotation.BizLog;
import com.inteink.modules.biz.model.enums.BizLogKind;
import com.inteink.modules.sys.entity.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务日志切面：环绕 @BizLog 方法，统一采集操作人/结果/异常，按 kind 分派给 handler 落库。
 * 用 @Order 让本切面在事务切面之外，保证失败也能独立落一条日志。
 */
@Slf4j
@Aspect
@Component
@Order(0)
public class BizLogAspect {

    /** 无登录主体（如定时/自动触发）时的操作人占位：-1=系统 */
    public static final Long SYSTEM_OPERATOR = -1L;

    private final Map<BizLogKind, BizLogHandler> handlers = new HashMap<>();

    public BizLogAspect(List<BizLogHandler> handlerList) {
        for (BizLogHandler h : handlerList) {
            handlers.put(h.kind(), h);
        }
    }

    @Around("@annotation(com.inteink.modules.biz.annotation.BizLog)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object ret = null;
        Throwable error = null;
        try {
            ret = pjp.proceed();
            return ret;
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            record(pjp, ret, error, System.currentTimeMillis() - start);
        }
    }

    private void record(JoinPoint pjp, Object ret, Throwable error, long cost) {
        try {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            BizLog bizLog = method.getAnnotation(BizLog.class);
            if (bizLog == null) {
                return;
            }
            BizLogHandler handler = handlers.get(bizLog.kind());
            if (handler == null) {
                log.warn("无对应 BizLogHandler: {}", bizLog.kind());
                return;
            }
            handler.handle(new BizLogContext(pjp, bizLog, ret, error, cost, currentOperator()));
        } catch (Exception e) {
            log.error("业务日志记录失败", e);
        }
    }

    private Long currentOperator() {
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            if (principal instanceof SysUserEntity) {
                return ((SysUserEntity) principal).getUserId();
            }
        } catch (Exception ignore) {
            // 无 Shiro 上下文（定时线程等）
        }
        return SYSTEM_OPERATOR;
    }
}
