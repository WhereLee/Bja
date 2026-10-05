package com.inteink.modules.biz.service.lifting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.form.ConverterForm; // 后续会写，先定义

/**
 * 转换器Service接口（基础CRUD+业务校验）
 */
public interface ConverterService extends IService<BizConverter> {

    // 1. 分页查询转换器列表（支持关联升降杆名称）
    PageUtils queryPage(ConverterForm form);

    // 2. 新增转换器（含硬件参数校验+关联升降杆存在性校验）
    boolean saveConverter(BizConverter converter);

    // 3. 修改转换器（同新增的校验逻辑）
    boolean updateConverter(BizConverter converter);
    // 新增：查询单条转换器（按转换器ID），用于显示是否有效
    BizConverter getConverterById(Long converterId);
}