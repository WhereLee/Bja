package com.inteink.modules.biz.exception;

import com.inteink.common.utils.Result;
import lombok.Getter;
import org.apache.http.HttpStatus;

/**
 * 升降策略业务专属异常（继承框架通用异常，适配Result的状态码规范）
 */
@Getter
public class StrategyBizException extends RuntimeException {

    /**
     * 错误码（严格适配Result的状态码：0=成功，400=参数错误，500=系统错误，403=业务规则错误）
     */
    private final Integer code;

    /**
     * 错误提示信息
     */
    private final String msg;

    /**
     * 构造方法（仅传提示信息，默认参数错误码400）
     */
    public StrategyBizException(String msg) {
        super(msg);
        this.code = HttpStatus.SC_BAD_REQUEST; // 400-参数错误
        this.msg = msg;
    }

    /**
     * 构造方法（自定义错误码+提示信息）
     */
    public StrategyBizException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 快速构建参数错误异常（400）
     */
    public static StrategyBizException paramError(String msg) {
        return new StrategyBizException(HttpStatus.SC_BAD_REQUEST, msg);
    }

    /**
     * 快速构建业务规则错误异常（403）
     */
    public static StrategyBizException ruleError(String msg) {
        return new StrategyBizException(HttpStatus.SC_FORBIDDEN, msg);
    }

    /**
     * 快速构建系统错误异常（500）
     */
    public static StrategyBizException systemError(String msg) {
        return new StrategyBizException(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    /**
     * 快速构建数据不存在异常（404）
     */
    public static StrategyBizException notFoundError(String msg) {
        return new StrategyBizException(HttpStatus.SC_NOT_FOUND, msg);
    }
}