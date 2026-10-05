package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.model.enums.BizLogKind;

/**
 * 业务日志处理器：按 kind 分派，各自把上下文写成一行日志。
 */
public interface BizLogHandler {

    BizLogKind kind();

    void handle(BizLogContext context);
}
