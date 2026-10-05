package com.inteink.modules.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.form.ConverterForm;

/**
 * 转换器服务：设备台账 CRUD + 与杆的绑定/解绑。
 */
public interface ConverterService extends IService<BizConverter> {

    Long saveConverter(BizConverter converter, Long operator);

    void updateConverter(BizConverter converter);

    void removeConverter(Long converterId);

    PageUtils queryPage(ConverterForm form);

    /** 把转换器绑定到某根杆 */
    void bind(Long converterId, Long rodId);

    /** 解绑（rod_id 置空） */
    void unbind(Long converterId);
}
