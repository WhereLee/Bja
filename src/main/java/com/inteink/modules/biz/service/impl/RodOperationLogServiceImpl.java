package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.modules.biz.mapper.BizLiftingRodLogMapper;
import com.inteink.modules.biz.model.entity.BizLiftingRodLog;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.model.enums.RodResultEnum;
import com.inteink.modules.biz.service.RodOperationLogService;
import org.springframework.stereotype.Service;

@Service
public class RodOperationLogServiceImpl extends ServiceImpl<BizLiftingRodLogMapper, BizLiftingRodLog>
        implements RodOperationLogService {

    @Override
    public void record(Long rodId, Integer action, RodLogTypeEnum type, Long strategyId, boolean success) {
        BizLiftingRodLog log = new BizLiftingRodLog();
        log.setRodId(rodId);
        log.setLogAction(action);
        log.setLogType(type.getCode());
        log.setStrategyId(strategyId);
        log.setLogResult(success ? RodResultEnum.SUCCESS.getCode() : RodResultEnum.FAIL.getCode());
        log.setLogOperateTime(System.currentTimeMillis() / 1000);
        this.save(log);
    }
}
