package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.annotation.TimeCost;
import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;
import com.inteink.modules.biz.mapper.BizLiftingStrategyRodMapper;
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
    @TimeCost("策略批量驱动")
    public int executeByStrategy(Long strategyId) {
        if (strategyId == null) {
            log.warn("【策略执行】strategyId 为空，跳过");
            return 0;
        }
        BizLiftingStrategy strategy = strategyMapper.selectById(strategyId);
        if (strategy == null || !strategy.isValid()) {
            log.warn("【策略执行】策略不存在或已删除，strategyId={}", strategyId);
            return 0;
        }
        Integer action = strategy.getStrategyAction();
        if (!RodActionEnum.isValid(action)) {
            log.warn("【策略执行】策略动作非法，strategyId={}, action={}", strategyId, action);
            return 0;
        }
        List<BizLiftingStrategyRod> relations = strategyRodMapper.selectList(
                new LambdaQueryWrapper<BizLiftingStrategyRod>()
                        .eq(BizLiftingStrategyRod::getStrategyId, strategyId));
        int success = 0;
        for (BizLiftingStrategyRod rel : relations) {
            Long rodId = rel.getRodId();
            if (rodId == null) {
                continue;
            }
            try {
                liftingRodService.operateRod(rodId, action, RodLogTypeEnum.AUTO, strategyId);
                success++;
            } catch (Exception e) {
                log.error("【策略执行】单杆驱动失败，strategyId={}, rodId={}", strategyId, rodId, e);
            }
        }
        log.info("【策略执行】完成，strategyId={}, 动作={}, 成功 {}/{}",
                strategyId, RodActionEnum.descOf(action), success, relations.size());
        return success;
    }
}
