package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyRod;
import com.inteink.modules.biz.mapper.BizLiftingStrategyRodMapper;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BizLiftingStrategyRodServiceImpl extends ServiceImpl<BizLiftingStrategyRodMapper, BizLiftingStrategyRod> implements BizLiftingStrategyRodService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindStrategyRods(Long strategyId, List<Long> rodIds) {
        // 1. 先删除该策略旧的关联关系
        removeByStrategyId(strategyId);

        // 2. 批量插入新关联关系
        if (rodIds == null || rodIds.isEmpty()) {
            return true; // 无升降杆绑定，直接返回成功
        }
        List<BizLiftingStrategyRod> list = new ArrayList<>();
        for (Long rodId : rodIds) {
            BizLiftingStrategyRod rod = new BizLiftingStrategyRod();
            rod.setStrategyId(strategyId);
            rod.setRodId(rodId);
            list.add(rod);
        }
        return saveBatch(list);
    }

    @Override
    public boolean removeByStrategyId(Long strategyId) {
        LambdaQueryWrapper<BizLiftingStrategyRod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLiftingStrategyRod::getStrategyId, strategyId);
        return remove(wrapper);
    }

    @Override
    public List<Long> getRodIdsByStrategyId(Long strategyId) {
        LambdaQueryWrapper<BizLiftingStrategyRod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLiftingStrategyRod::getStrategyId, strategyId)
                .select(BizLiftingStrategyRod::getRodId);
        return listObjs(wrapper, obj -> Long.parseLong(obj.toString()));
    }


}