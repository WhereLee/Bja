package com.inteink.modules.biz.service.strategy;

import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;

public interface LiftingStrategyAuditService {
//    StrategyResponseVO auditStrategy(StrategyAuditDTO auditDTO);
      Result<StrategyResponseVO> auditStrategy(StrategyAuditDTO dto);
}