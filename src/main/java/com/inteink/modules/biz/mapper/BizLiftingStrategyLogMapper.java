package com.inteink.modules.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 升降策略日志表Mapper（仅基础CRUD）
 */
@Mapper
public interface BizLiftingStrategyLogMapper extends BaseMapper<BizLiftingStrategyLog> {
}