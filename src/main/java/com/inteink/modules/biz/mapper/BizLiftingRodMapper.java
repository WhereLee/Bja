package com.inteink.modules.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;

public interface BizLiftingRodMapper extends BaseMapper<BizLiftingRod> {
    IPage<BizLiftingRod> queryRodPage(IPage<BizLiftingRod> page, @Param("form") LiftingRodForm form);

    List<BizLiftingRod> selectValidRodByStrategyId(@Param("strategyId") Long strategyId);
}