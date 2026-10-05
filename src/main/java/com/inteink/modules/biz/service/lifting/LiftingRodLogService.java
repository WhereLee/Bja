package com.inteink.modules.biz.service.lifting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingRodLog;
import com.inteink.modules.biz.model.form.LiftingRodForm;

public interface LiftingRodLogService extends IService<BizLiftingRodLog> {
    PageUtils queryLogPage(LiftingRodForm form, Long userId);
}