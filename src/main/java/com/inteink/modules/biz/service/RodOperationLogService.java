package com.inteink.modules.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.modules.biz.model.entity.BizLiftingRodLog;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;

/**
 * 杆动作日志服务。
 */
public interface RodOperationLogService extends IService<BizLiftingRodLog> {

    /**
     * 记录一次杆动作。
     *
     * @param rodId      杆ID
     * @param action     动作 1-升 2-降
     * @param type       类型 手动/自动
     * @param strategyId 来源策略（自动时有值，可空）
     * @param success    是否成功
     */
    void record(Long rodId, Integer action, RodLogTypeEnum type, Long strategyId, boolean success);
}
