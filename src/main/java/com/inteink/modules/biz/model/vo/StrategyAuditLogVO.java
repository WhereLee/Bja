package com.inteink.modules.biz.model.vo;

import lombok.Data;

/**
 * 策略审核日志专用VO
 * 专门封装审核日志所需字段，便于后续格式调整与维护
 */
@Data
public class StrategyAuditLogVO {
    /** 策略ID */
    private Long strategyId;
    /** 策略名 */
    private String strategyName;
    /** 审核人ID */
    private Long auditorId;
    /** 审核结果（如：审核通过/审核驳回） */
    private String auditResult;
    /** 驳回原因 */
    private String rejectReason;

    /**
     * 静态构建方法：封装字段赋值+兜底逻辑，避免业务代码重复判空
     * @param strategyId 策略ID
     * @param strategyName 策略名
     * @param auditorId 审核人ID
     * @param auditResult 审核结果描述
     * @param rejectReason 驳回原因
     * @return 审核日志VO
     */
    public static StrategyAuditLogVO build(Long strategyId, String strategyName, Long auditorId, String auditResult, String rejectReason) {
        StrategyAuditLogVO logVO = new StrategyAuditLogVO();
        // 字段兜底：避免空值导致日志格式异常
        logVO.setStrategyId(strategyId == null ? 0L : strategyId);
        logVO.setStrategyName(strategyName == null || strategyName.trim().isEmpty() ? "未知策略名" : strategyName.trim());
        logVO.setAuditorId(auditorId == null ? 0L : auditorId);
        logVO.setAuditResult(auditResult == null || auditResult.trim().isEmpty() ? "未知审核结果" : auditResult.trim());
        // 审核通过时，驳回原因固定为"审核通过"；驳回时若未填原因，兜底为"未填写驳回原因"
        if ("审核通过".equals(logVO.getAuditResult())) {
            logVO.setRejectReason("审核通过");
        } else {
            logVO.setRejectReason(rejectReason == null || rejectReason.trim().isEmpty() ? "未填写驳回原因" : rejectReason.trim());
        }
        return logVO;
    }
}