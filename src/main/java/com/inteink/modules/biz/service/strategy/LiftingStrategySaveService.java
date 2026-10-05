package com.inteink.modules.biz.service.strategy;

import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;

public interface LiftingStrategySaveService {
    StrategyResponseVO saveStrategyWithDetail(StrategySaveDTO saveDTO);
}