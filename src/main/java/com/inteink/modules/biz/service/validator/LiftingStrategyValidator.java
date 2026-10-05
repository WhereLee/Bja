package com.inteink.modules.biz.service.validator;

import com.inteink.common.exception.RRException;
import com.inteink.modules.biz.constant.LiftingStrategyConstant;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 升降策略校验器
 * 适配项目规范：异常抛出符合RRException定义；常量/枚举引用匹配；校验逻辑内聚
 */
@Slf4j
@Component
public class LiftingStrategyValidator {

    /**
     * 策略存在+未删除校验（核心通用校验）
     */
    public void validateStrategyExistAndNotDeleted(BizLiftingStrategy strategy, Long strategyId) {
        if (strategy == null) {
            throw new RRException("策略不存在", 404);
        }
        if (LiftingStrategyConstant.STRATEGY_STATUS_DELETED.equals(strategy.getStrategyStatus())) {
            log.warn("【策略校验】策略已逻辑删除，策略ID：{}", strategyId);
            throw new RRException("策略已逻辑删除，不允许操作", 400);
        }
    }

    /**
     * 审核前置规则校验（待审核状态）
     */
    public void validateStrategyAuditPreRule(BizLiftingStrategy strategy) {
        Integer currentState = strategy.getStrategyCheckState();
        if (!LiftingStrategyConstant.CHECK_STATE_PENDING.equals(currentState)) {
            String stateDesc = BizLiftingStrategyEnum.getCheckStateDesc(currentState);
            throw new RRException("该策略当前审核状态为【" + stateDesc + "】，禁止重复审核", 400);
        }
    }

    /**
     * 策略状态有效性校验（仅有效状态可操作）
     */
    public void validateStrategyValidStatus(BizLiftingStrategy strategy) {
        if (!LiftingStrategyConstant.STRATEGY_STATUS_VALID.equals(strategy.getStrategyStatus())) {
            String statusDesc = BizLiftingStrategyEnum.getStrategyStatusDesc(strategy.getStrategyStatus());
            throw new RRException("策略当前为【" + statusDesc + "】状态，禁止执行该操作", 400);
        }
    }

    /**
     * 策略执行前置校验（有效+审核通过）
     */
    public void validateStrategyExecutePreRule(BizLiftingStrategy strategy) {
        // 1. 校验有效状态
        validateStrategyValidStatus(strategy);
        // 2. 校验审核通过
        Integer checkState = strategy.getStrategyCheckState();
        if (!LiftingStrategyConstant.CHECK_STATE_PASS.equals(checkState)) {
            String checkDesc = BizLiftingStrategyEnum.getCheckStateDesc(checkState);
            throw new RRException("策略当前为【" + checkDesc + "】状态，禁止执行", 400);
        }
    }
}