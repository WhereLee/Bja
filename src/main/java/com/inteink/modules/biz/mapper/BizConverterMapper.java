package com.inteink.modules.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.form.ConverterForm;
import org.apache.ibatis.annotations.Param;

/**
 * 转换器Mapper
 */
public interface BizConverterMapper extends BaseMapper<BizConverter> {
    // 分页查询（后续XML实现关联升降杆名称）
    IPage<BizConverter> queryConverterPage(IPage<BizConverter> page, @Param("form") ConverterForm form);
}