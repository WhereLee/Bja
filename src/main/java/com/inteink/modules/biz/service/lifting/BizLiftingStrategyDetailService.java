package com.inteink.modules.biz.service.lifting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;

import java.util.List;

/**
 * 升降策略明细表Service
 */
public interface BizLiftingStrategyDetailService extends IService<BizLiftingStrategyDetail> {

    /**
     * 根据策略ID查询明细列表
     */
    List<BizLiftingStrategyDetail> getByStrategyId(Long strategyId);

    /**
     * 根据策略ID删除所有明细
     */
    boolean removeByStrategyId(Long strategyId);
}