package com.inteink.modules.biz.constant;

import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;

public interface LiftingStrategyConstant {

    // ========== 枚举联动常量 ==========
    Long STRATEGY_STATUS_VALID = BizLiftingStrategyEnum.STRATEGY_STATUS_VALID.getStrategyStatusCode();
    Long STRATEGY_STATUS_DELETED = BizLiftingStrategyEnum.STRATEGY_STATUS_DELETED.getStrategyStatusCode();
    Long STRATEGY_STATUS_PAUSED = BizLiftingStrategyEnum.STRATEGY_STATUS_PAUSED.getStrategyStatusCode();

    Integer CHECK_STATE_PENDING = BizLiftingStrategyEnum.CHECK_STATE_PENDING.getCheckStateCode();
    Integer CHECK_STATE_PASS = BizLiftingStrategyEnum.CHECK_STATE_PASS.getCheckStateCode();
    Integer CHECK_STATE_REJECT = BizLiftingStrategyEnum.CHECK_STATE_REJECT.getCheckStateCode();

    Integer ACTION_LIFT = BizLiftingStrategyEnum.ACTION_LIFT.getActionCode();
    Integer ACTION_LOWER = BizLiftingStrategyEnum.ACTION_LOWER.getActionCode();

    Integer LOG_TYPE_ADD = BizLiftingStrategyEnum.LOG_TYPE_ADD.getLogTypeCode();
    Integer LOG_TYPE_UPDATE = BizLiftingStrategyEnum.LOG_TYPE_UPDATE.getLogTypeCode();
    Integer LOG_TYPE_AUDIT = BizLiftingStrategyEnum.LOG_TYPE_AUDIT.getLogTypeCode();
    Integer LOG_TYPE_PAUSE = BizLiftingStrategyEnum.LOG_TYPE_PAUSE.getLogTypeCode();
    Integer LOG_TYPE_RESUME = BizLiftingStrategyEnum.LOG_TYPE_RESUME.getLogTypeCode();
    Integer LOG_TYPE_EXECUTE = BizLiftingStrategyEnum.LOG_TYPE_EXECUTE.getLogTypeCode();
    Integer LOG_TYPE_REMOVE = BizLiftingStrategyEnum.LOG_TYPE_REMOVE.getLogTypeCode();

    // ========== 默认值常量 ==========
    Long DEFAULT_OPERATOR_ID = 0L;
    String DEFAULT_STRATEGY_NAME = "默认策略";
    String EMPTY_REMARK = "";
    String DEFAULT_REJECT_REASON = "未填写驳回原因";
    String UNKNOWN_DESC = "未知";
    String VALID_STATUS_DESC = "有效";

    // ========== 业务规则常量 ==========
    Integer DETAIL_BATCH_SAVE_SIZE = 500;
    String TIME_FORMAT_REGEX = "^([01]\\d|2[0-3]):[0-5]\\d$";
    String SKIP_REASON_ROD_NOT_EXIST = "升降杆不存在";
    String SKIP_REASON_ROD_INVALID = "升降杆已失效";

    // ========== 通用配置常量 ==========
    String TIME_SEPARATOR = ",";
    String TIME_CONNECTOR = "-";
    String ROD_INFO_SEPARATOR = "、";
    String LOG_PLACEHOLDER_PREFIX = "｜";
    String BASE_LOG_FORMAT = "%s！strategyId=%s";
}