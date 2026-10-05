package com.inteink.modules.biz.service.strategy;

import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;

public interface LiftingStrategyUpdateService {
    StrategyResponseVO updateStrategyWithDetail(StrategyUpdateDTO updateDTO);
}