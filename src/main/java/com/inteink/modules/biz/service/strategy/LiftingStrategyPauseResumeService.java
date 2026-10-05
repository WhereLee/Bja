package com.inteink.modules.biz.service.strategy;

import com.inteink.modules.biz.model.vo.StrategyResponseVO;

public interface LiftingStrategyPauseResumeService {
    StrategyResponseVO pauseStrategy(Long strategyId, Long operator);
    StrategyResponseVO resumeStrategy(Long strategyId, Long operator);
}