package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingRodLog;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.mapper.BizLiftingRodLogMapper;
import com.inteink.modules.biz.service.lifting.LiftingRodLogService;
import org.springframework.stereotype.Service;

@Service
public class LiftingRodLogServiceImpl extends ServiceImpl<BizLiftingRodLogMapper, BizLiftingRodLog> implements LiftingRodLogService {

    @Override
    public PageUtils queryLogPage(LiftingRodForm form, Long userId) {
        LambdaQueryWrapper<BizLiftingRodLog> wrapper = new LambdaQueryWrapper<>();
        if (form.getRodId() != null) {
            wrapper.eq(BizLiftingRodLog::getRodId, form.getRodId());
        }
        if (form.getLogType() != null) {
            wrapper.eq(BizLiftingRodLog::getLogType, form.getLogType());
        }
        if (form.getLogAction() != null) {
            wrapper.eq(BizLiftingRodLog::getLogAction, form.getLogAction());
        }

        // 此时this.page()泛型匹配（绑定的是BizLiftingRodLog）
        IPage<BizLiftingRodLog> page = this.page(
                new Page<>(form.getPageNum(), form.getPageSize()),
                wrapper
        );
        return new PageUtils(page);
    }
}