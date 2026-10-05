package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.mapper.BizLiftingStrategyDetailMapper;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 升降策略明细表Service实现
 */
@Service
public class BizLiftingStrategyDetailServiceImpl extends ServiceImpl<BizLiftingStrategyDetailMapper, BizLiftingStrategyDetail>
        implements BizLiftingStrategyDetailService {

    /**
     * 根据策略ID查询明细列表
     */
    @Override
    public List<BizLiftingStrategyDetail> getByStrategyId(Long strategyId) {
        LambdaQueryWrapper<BizLiftingStrategyDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLiftingStrategyDetail::getStrategyId, strategyId);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据策略ID删除所有明细
     */
    @Override
    public boolean removeByStrategyId(Long strategyId) {
        LambdaQueryWrapper<BizLiftingStrategyDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLiftingStrategyDetail::getStrategyId, strategyId);
        return baseMapper.delete(wrapper) > 0;
    }
    
}