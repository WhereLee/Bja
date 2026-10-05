package com.inteink.modules.biz.service.lifting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;

import java.util.List;

/**
 * 升降策略日志表Service
 */
public interface BizLiftingStrategyLogService extends IService<BizLiftingStrategyLog> {

    /**
     * 根据策略ID查询操作日志列表
     */
    List<BizLiftingStrategyLog> getLogByStrategyId(Long strategyId);

    /**
     * 新增操作日志（封装通用逻辑）
     */
    boolean saveStrategyLog(Long strategyId, Integer logType, String logRemark, Long operatorId);
}