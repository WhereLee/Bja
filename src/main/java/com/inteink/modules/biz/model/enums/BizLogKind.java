package com.inteink.modules.biz.model.enums;

/**
 * 业务日志类型（决定由哪个 handler 处理、落哪张表）。
 */
public enum BizLogKind {
    /** 杆动作日志 → biz_lifting_rod_log */
    ROD_OP,
    /** 策略审核/操作日志 → biz_lifting_strategy_log */
    STRATEGY_AUDIT
}
