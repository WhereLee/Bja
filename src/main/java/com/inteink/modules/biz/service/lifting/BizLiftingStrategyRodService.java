package com.inteink.modules.biz.service.lifting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyRod;

import java.util.List;

public interface BizLiftingStrategyRodService extends IService<BizLiftingStrategyRod> {
    /**
     * 批量绑定策略-升降杆
     * @param strategyId 策略ID
     * @param rodIds 升降杆ID列表
     * @return 是否成功
     */
    boolean bindStrategyRods(Long strategyId, List<Long> rodIds);

    /**
     * 根据策略ID删除关联关系
     * @param strategyId 策略ID
     * @return 是否成功
     */
    boolean removeByStrategyId(Long strategyId);

    /**
     * 根据策略ID查询关联的升降杆ID列表
     * @param strategyId 策略ID
     * @return 升降杆ID列表
     */
    List<Long> getRodIdsByStrategyId(Long strategyId);
}