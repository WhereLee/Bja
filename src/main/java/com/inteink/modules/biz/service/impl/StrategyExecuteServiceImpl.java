package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.annotation.TimeCost;
import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;
import com.inteink.modules.biz.mapper.BizLiftingStrategyRodMapper;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyRod;
import com.inteink.modules.biz.model.enums.RodActionEnum;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.service.LiftingRodService;
import com.inteink.modules.biz.service.StrategyExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyExecuteServiceImpl implements StrategyExecuteService {

    private final BizLiftingStrategyMapper strategyMapper;
    private final BizLiftingStrategyRodMapper strategyRodMapper;
    private final LiftingRodService liftingRodService;

    @Override
    public int executeByStrategy(Long strategyId) {
        // 立即执行：按策略主动作执行（视作 BEGIN，不做前置守卫）
        BizLiftingStrategy strategy = strategyId == null ? null : strategyMapper.selectById(strategyId);
        if (strategy == null || !strategy.isValid()) {
            log.warn("【策略执行】策略不存在或已删除，strategyId={}", strategyId);
            return 0;
        }
        return executeByStrategy(strategyId, strategy.getStrategyAction(), "BEGIN");
    }

    @Override
    @TimeCost("策略批量驱动")
    public int executeByStrategy(Long strategyId, Integer action, String nodeType) {
        if (strategyId == null) {
            log.warn("【策略执行】strategyId 为空，跳过");
            return 0;
        }
        BizLiftingStrategy strategy = strategyMapper.selectById(strategyId);
        if (strategy == null || !strategy.isValid()) {
            log.warn("【策略执行】策略不存在或已删除，strategyId={}", strategyId);
            return 0;
        }
        if (!RodActionEnum.isValid(action)) {
            log.warn("【策略执行】动作非法，strategyId={}, action={}", strategyId, action);
            return 0;
        }
        boolean endNode = "END".equalsIgnoreCase(nodeType);
        // END 前置守卫：仅当杆处于 begin 期望态(strategy.action)时才执行反向，避免“只降不升”
        Integer expectedState = strategy.getStrategyAction();
        List<BizLiftingStrategyRod> relations = strategyRodMapper.selectList(
                new LambdaQueryWrapper<BizLiftingStrategyRod>()
                        .eq(BizLiftingStrategyRod::getStrategyId, strategyId));
        int success = 0;
        for (BizLiftingStrategyRod rel : relations) {
            Long rodId = rel.getRodId();
            if (rodId == null) {
                continue;
            }
            if (endNode) {
                BizLiftingRod rod = liftingRodService.getById(rodId);
                if (rod == null || !expectedState.equals(rod.getRodState())) {
                    log.warn("【策略执行】END跳过 rod={} 未处于预期态{}（本轮begin可能未执行/审核晚于begin）",
                            rodId, expectedState);
                    continue;
                }
            }
            try {
                liftingRodService.operateRod(rodId, action, RodLogTypeEnum.AUTO, strategyId);
                success++;
            } catch (Exception e) {
                log.error("【策略执行】单杆驱动失败，strategyId={}, rodId={}", strategyId, rodId, e);
            }
        }
        log.info("【策略执行】完成，strategyId={}, node={}, 动作={}, 成功 {}/{}",
                strategyId, nodeType, RodActionEnum.descOf(action), success, relations.size());
        return success;
    }
}
