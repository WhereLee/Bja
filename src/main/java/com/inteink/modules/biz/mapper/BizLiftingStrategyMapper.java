package com.inteink.modules.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 升降策略主表Mapper（仅主表操作，无多表）
 */
@Mapper
public interface BizLiftingStrategyMapper extends BaseMapper<BizLiftingStrategy> {
    /**
     * 查询所有有效的自动策略（已审核通过+未失效）
     */
    List<BizLiftingStrategy> selectValidStrategies();

    /**
     * 按策略名称统计有效策略的数量（用于重复校验）
     * @param strategyName 策略名称
     * @return 数量
     */
    int countByStrategyName(@Param("strategyName") String strategyName);
}