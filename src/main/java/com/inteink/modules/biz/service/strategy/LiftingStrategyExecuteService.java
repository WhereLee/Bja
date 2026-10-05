package com.inteink.modules.biz.service.strategy;

import com.inteink.modules.biz.model.vo.StrategyExecuteResultVO;

public interface LiftingStrategyExecuteService {
    StrategyExecuteResultVO execute(Long strategyId, Integer action);
}