package com.inteink.modules.biz.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.common.exception.RRException;
import com.inteink.modules.biz.assembler.StrategyAssembler;
import com.inteink.modules.biz.constant.LiftingStrategyConstant;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.validator.LiftingStrategyValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;

// ✅ 仅移除@Component注解，其余代码完全不变
@Slf4j
public abstract class AbstractLiftingStrategyService {

    @Autowired
    protected BizLiftingStrategyMapper strategyMapper;

    @Autowired
    protected LiftingStrategyValidator strategyValidator;

    @Autowired
    protected StrategyAssembler strategyAssembler;

    protected Long getCurrentTimestamp() {
        return Instant.now().getEpochSecond();
    }

    protected BizLiftingStrategy getStrategyById(Long strategyId) {
        if (strategyId == null || strategyId <= 0) {
            throw StrategyBizException.paramError("策略ID非法");
        }
        BizLiftingStrategy strategy = strategyMapper.selectById(strategyId);
        strategyValidator.validateStrategyExistAndNotDeleted(strategy, strategyId);
        return strategy;
    }

    protected void validateStrategyNotDeleted(BizLiftingStrategy strategy, Long strategyId) {
        if (LiftingStrategyConstant.STRATEGY_STATUS_DELETED.equals(strategy.getStrategyStatus())) {
            log.warn("strategyId:{} 已逻辑删除，禁止操作", strategyId);
            throw new RRException("策略已逻辑删除，不允许操作", 400);
        }
    }

    protected void validateAuditPending(BizLiftingStrategy strategy) {
        if (!LiftingStrategyConstant.CHECK_STATE_PENDING.equals(strategy.getStrategyCheckState())) {
            String desc = LiftingStrategyConstant.CHECK_STATE_PASS.equals(strategy.getStrategyCheckState()) ? "已通过" : "已驳回";
            throw StrategyBizException.ruleError("该策略当前审核状态为" + desc + "，禁止重复操作");
        }
    }

    protected StrategyResponseVO buildSuccessVO(BizLiftingStrategy strategy) {
        StrategyResponseVO vo = strategyAssembler.assembleSimpleResponseVO(strategy, null);
        if (vo.getStrategyStatus() != null && vo.getStrategyStatus().equals(LiftingStrategyConstant.STRATEGY_STATUS_VALID)) {
            vo.setStrategyStatusDesc(LiftingStrategyConstant.VALID_STATUS_DESC);
        }
        if (vo.getRodList() == null) {
            vo.setRodList(new ArrayList<>());
        }
        if (vo.getDetailList() == null) {
            vo.setDetailList(new ArrayList<>());
        }
        if (vo.getStrategyCheckStateDesc() == null || vo.getStrategyCheckStateDesc().isEmpty()) {
            vo.setStrategyCheckStateDesc(BizLiftingStrategyEnum.getCheckStateDesc(vo.getStrategyCheckState()));
        }
        if (vo.getStrategyStatusDesc() == null || vo.getStrategyStatusDesc().isEmpty()) {
            vo.setStrategyStatusDesc(BizLiftingStrategyEnum.getStrategyStatusDesc(vo.getStrategyStatus()));
        }
        return vo;
    }

    protected LambdaQueryWrapper<BizLiftingStrategy> buildBaseQueryWrapper() {
        LambdaQueryWrapper<BizLiftingStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(BizLiftingStrategy::getStrategyStatus, LiftingStrategyConstant.STRATEGY_STATUS_DELETED);
        return wrapper;
    }

    protected void fillBaseUpdateFields(BizLiftingStrategy strategy) {
        strategy.setStrategyUpdatetime(getCurrentTimestamp());
    }

    protected void fillBaseCreateFields(BizLiftingStrategy strategy, Long creatorId) {
        long now = getCurrentTimestamp();
        strategy.setStrategyCreatetime(now);
        strategy.setStrategyUpdatetime(now);
        strategy.setStrategyCreator(creatorId);
        strategy.setStrategyStatus(LiftingStrategyConstant.STRATEGY_STATUS_VALID);
        strategy.setStrategyCheckState(LiftingStrategyConstant.CHECK_STATE_PENDING);
    }
}