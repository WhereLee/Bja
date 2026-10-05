package com.inteink.modules.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyRod;

import java.util.List;

public interface BizLiftingStrategyRodMapper extends BaseMapper<BizLiftingStrategyRod> {
    /**
     * 根据策略ID查询关联的升降杆
     */
    List<BizLiftingStrategyRod> selectByStrategyId(Long strategyId);
}