package com.inteink.modules.biz.service.strategy;

import com.inteink.modules.biz.model.vo.StrategyResponseVO;

public interface LiftingStrategyRemoveService {
    StrategyResponseVO removeStrategyWithDetail(Long strategyId);
}