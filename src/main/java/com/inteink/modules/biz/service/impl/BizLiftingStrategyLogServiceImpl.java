package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.mapper.BizLiftingStrategyLogMapper;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 升降策略日志表Service实现
 */
@Service
public class BizLiftingStrategyLogServiceImpl extends ServiceImpl<BizLiftingStrategyLogMapper, BizLiftingStrategyLog>
        implements BizLiftingStrategyLogService {

    /**
     * 根据策略ID查询日志列表
     */
    @Override
    public List<BizLiftingStrategyLog> getLogByStrategyId(Long strategyId) {
        LambdaQueryWrapper<BizLiftingStrategyLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLiftingStrategyLog::getStrategyId, strategyId)
                .orderByDesc(BizLiftingStrategyLog::getLogOperateTime); // 按操作时间倒序
        return baseMapper.selectList(wrapper);
    }

    /**
     * 通用新增日志方法（封装重复逻辑）
     */
    @Override
    public boolean saveStrategyLog(Long strategyId, Integer logType, String logRemark, Long operatorId) {
        BizLiftingStrategyLog log = new BizLiftingStrategyLog();
        log.setStrategyId(strategyId);
        log.setLogType(logType);
        log.setLogRemark(logRemark);
        log.setLogOperator(operatorId);
        log.setLogOperateTime(System.currentTimeMillis() / 1000); // 秒级时间戳
        return baseMapper.insert(log) > 0;
    }
}