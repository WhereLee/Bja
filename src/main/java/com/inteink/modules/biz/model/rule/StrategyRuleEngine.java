package com.inteink.modules.biz.model.rule;

import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.exception.StrategyBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 升降策略规则引擎（参数校验+业务规则校验）
 * 核心价值：统一管控基础格式+业务语义校验，避免校验逻辑散落在各层
 */
@Slf4j
@Component
public class StrategyRuleEngine {

    // ========== 基础常量（集中管理，便于维护） ==========
    // 动作类型有效值：1-升杆，2-降杆
    private static final List<Integer> VALID_ACTION_TYPES = Arrays.asList(1, 2);
    // 执行类型有效值：1-每日，2-每周，3-每月，4-指定日期
    private static final List<Integer> VALID_EXECUTE_TYPES = Arrays.asList(1, 2, 3, 4);
    // 时段格式正则：兼容单时间(HH:mm)、双时间(HH:mm,HH:mm)
    private static final Pattern TIME_PERIOD_PATTERN = Pattern.compile("^(\\d{2}:\\d{2})(,\\d{2}:\\d{2})?$");
    // 基础时间格式正则：HH:mm
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");
    // 指定日期格式正则：yyyy-MM-dd HH:mm（增强校验）
    private static final Pattern APPOINT_DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$");

    @Autowired
    private Validator validator;

    /**
     * 新增策略参数校验（基础+业务全量校验）
     */
    public void validateSaveDTO(StrategySaveDTO dto) {
        log.info("开始校验新增策略参数，入参：{}", dto);
        // 1. 核心业务字段合法性校验
        validateActionType(dto.getStrategyAction());
        validateExecuteType(dto.getStrategyType());
        validateTimePeriod(dto.getDetailTime());
        validateExecuteRule(dto.getStrategyType(), dto.getStrategyDates());
        // 2. 可选参数校验（有值则校验）
        if (StringUtils.isNotBlank(dto.getRodIds())) {
            validateRodIds(dto.getRodIds());
        }
        log.info("新增策略参数校验通过");
    }

    /**
     * 修改策略参数校验（仅校验有值的字段）
     */
    public void validateUpdateDTO(StrategyUpdateDTO dto) {
        log.info("开始校验修改策略参数，入参：{}", dto);
        // 1. 有值的核心字段才校验
        if (dto.getStrategyAction() != null) {
            validateActionType(dto.getStrategyAction());
        }
        if (dto.getStrategyType() != null) {
            validateExecuteType(dto.getStrategyType());
        }
        if (StringUtils.isNotBlank(dto.getDetailTime())) {
            validateTimePeriod(dto.getDetailTime());
        }
        if (StringUtils.isNotBlank(dto.getStrategyDates()) && dto.getStrategyType() != null) {
            validateExecuteRule(dto.getStrategyType(), dto.getStrategyDates());
        }
        // 2. 可选参数校验
        if (StringUtils.isNotBlank(dto.getRodIds())) {
            validateRodIds(dto.getRodIds());
        }
        log.info("修改策略参数校验通过");
    }

    /**
     * 审核策略参数校验（基础校验+状态合法性+驳回原因校验）
     */
    public void validateAuditDTO(StrategyAuditDTO dto) {
        log.info("开始校验审核策略参数，入参：{}", dto);
        // 1. JSR303基础校验（自动拦截空值）
        Set<ConstraintViolation<StrategyAuditDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMsg = violations.iterator().next().getMessage();
            log.error("审核策略基础参数校验失败：{}", errorMsg);
            throw StrategyBizException.paramError(errorMsg);
        }
        // 2. 审核状态合法性校验
        if (!StrategyAuditDTO.VALID_CHECK_STATES.contains(dto.getCheckState())) {
            log.error("审核状态不合法，入参状态：{}", dto.getCheckState());
            throw StrategyBizException.paramError("审核状态只能是1（通过）或2（驳回）");
        }
        // 3. 新增：驳回时校验原因非空
        if (dto.getCheckState() == 2 && StringUtils.isBlank(dto.getReason())) {
            log.error("驳回策略未填写驳回原因，策略ID：{}", dto.getStrategyId());
            throw StrategyBizException.paramError("驳回策略必须填写驳回原因");
        }
        log.info("审核策略参数校验通过");
    }

    /**
     * 审核前置业务规则校验（已审核的策略不允许重复审核）
     */
    public void validateAuditPreRule(BizLiftingStrategy strategy) {
        log.info("开始校验审核前置规则，策略ID：{}，当前状态：{}", strategy.getStrategyId(), strategy.getStrategyCheckState());
        Integer currentState = strategy.getStrategyCheckState();
        if (currentState != 0) {
            String stateDesc = currentState == 1 ? "已通过" : "已驳回";
            log.error("策略重复审核，ID：{}，当前状态：{}", strategy.getStrategyId(), stateDesc);
            throw StrategyBizException.ruleError("该策略当前状态为" + stateDesc + "，不允许重复审核");
        }
        log.info("审核前置规则校验通过");
    }

    /**
     * 修改前置业务规则校验（已通过的策略不允许修改）
     */
    public void validateUpdatePreRule(BizLiftingStrategy strategy) {
        log.info("开始校验修改前置规则，策略ID：{}，当前状态：{}", strategy.getStrategyId(), strategy.getStrategyCheckState());
        Integer currentState = strategy.getStrategyCheckState();
        if (currentState == 1) {
            log.error("已通过策略修改，ID：{}", strategy.getStrategyId());
            throw StrategyBizException.ruleError("该策略已审核通过，不允许修改");
        }
        log.info("修改前置规则校验通过");
    }

    // ========== 私有校验方法（单一职责） ==========
    /**
     * 动作类型校验（1-升杆/2-降杆）
     */
    private void validateActionType(Integer actionType) {
        if (!VALID_ACTION_TYPES.contains(actionType)) {
            log.error("动作类型不合法，入参：{}", actionType);
            throw StrategyBizException.paramError("动作类型只能是1（升杆）或2（降杆）");
        }
    }

    /**
     * 执行类型校验（1-每日/2-每周/3-每月/4-指定日期）
     */
    private void validateExecuteType(Integer executeType) {
        if (!VALID_EXECUTE_TYPES.contains(executeType)) {
            log.error("执行类型不合法，入参：{}", executeType);
            throw StrategyBizException.paramError("执行类型只能是1（每日）、2（每周）、3（每月）、4（指定日期）");
        }
    }

    /**
     * 执行时段格式校验（兼容单/双时间）
     */
    private void validateTimePeriod(String timePeriod) {
        // ======== 新增：判空拦截（核心修复，杜绝空指针） ========
        if (timePeriod == null || timePeriod.trim().isEmpty()) {
            log.error("执行时段格式不合法，入参为null/空");
            throw StrategyBizException.paramError("执行时段格式错误，支持两种格式：单时间(HH:mm)、双时间(HH:mm,HH:mm)，示例：18:56 或 18:56,18:58");
        }
        // ======== 原有逻辑保留不变 ========
        if (!TIME_PERIOD_PATTERN.matcher(timePeriod).matches()) {
            log.error("执行时段格式不合法，入参：{}", timePeriod);
            throw StrategyBizException.paramError("执行时段格式错误，支持两种格式：单时间(HH:mm)、双时间(HH:mm,HH:mm)，示例：18:56 或 18:56,18:58");
        }
    }

    /**
     * 执行规则校验（按执行类型匹配格式，增强版）
     */
    private void validateExecuteRule(Integer executeType, String executeRule) {
        switch (executeType) {
            case 1: // 每日：HH:mm
                if (!TIME_PATTERN.matcher(executeRule).matches()) {
                    log.error("每日执行规则格式错误，入参：{}", executeRule);
                    throw StrategyBizException.paramError("每日执行规则格式错误，正确示例：20:00");
                }
                break;
            case 2: // 每周：1-7（周一到周日）
                try {
                    int week = Integer.parseInt(executeRule);
                    if (week < 1 || week > 7) {
                        log.error("每周执行规则范围错误，入参：{}", executeRule);
                        throw StrategyBizException.paramError("每周执行规则格式错误，只能是1-7（周一到周日）");
                    }
                } catch (NumberFormatException e) {
                    log.error("每周执行规则格式错误（非数字），入参：{}", executeRule, e);
                    throw StrategyBizException.paramError("每周执行规则格式错误，只能是数字1-7");
                }
                break;
            case 3: // 每月：1-31
                try {
                    int day = Integer.parseInt(executeRule);
                    if (day < 1 || day > 31) {
                        log.error("每月执行规则范围错误，入参：{}", executeRule);
                        throw StrategyBizException.paramError("每月执行规则格式错误，只能是1-31");
                    }
                } catch (NumberFormatException e) {
                    log.error("每月执行规则格式错误（非数字），入参：{}", executeRule, e);
                    throw StrategyBizException.paramError("每月执行规则格式错误，只能是数字1-31");
                }
                break;
            case 4: // 指定日期：yyyy-MM-dd HH:mm（增强精准校验）
                if (!APPOINT_DATE_PATTERN.matcher(executeRule).matches()) {
                    log.error("指定日期执行规则格式错误，入参：{}", executeRule);
                    throw StrategyBizException.paramError("指定日期执行规则格式错误，正确示例：2025-12-12 18:56");
                }
                break;
            default:
                log.error("执行类型不合法，入参：{}", executeType);
                throw StrategyBizException.paramError("执行类型不合法");
        }
    }

    /**
     * 杆ID格式校验（逗号分隔的纯数字）
     */
    private void validateRodIds(String rodIds) {
        String[] rodIdArr = rodIds.split(",");
        for (String rodIdStr : rodIdArr) {
            String trimRodId = rodIdStr.trim();
            try {
                Long.parseLong(trimRodId);
            } catch (NumberFormatException e) {
                log.error("杆ID格式错误，非法值：{}", trimRodId, e);
                throw StrategyBizException.paramError("杆ID格式错误：" + trimRodId + "不是有效数字");
            }
        }
    }
}