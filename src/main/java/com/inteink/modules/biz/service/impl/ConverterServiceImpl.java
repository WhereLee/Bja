package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.exception.RRException;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.StringUtils;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.form.ConverterForm;
import com.inteink.modules.biz.service.ConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConverterServiceImpl extends ServiceImpl<BizConverterMapper, BizConverter>
        implements ConverterService {

    private static final long STATUS_VALID = 0L;

    private final BizLiftingRodMapper rodMapper;

    @Override
    public Long saveConverter(BizConverter converter, Long operator) {
        if (StringUtils.isBlank(converter.getConverterSn())) {
            throw new RRException("设备SN不能为空");
        }
        Integer dup = this.count(new LambdaQueryWrapper<BizConverter>()
                .eq(BizConverter::getConverterSn, converter.getConverterSn())
                .eq(BizConverter::getConverterStatus, STATUS_VALID));
        if (dup != null && dup > 0) {
            throw new RRException("同SN转换器已存在");
        }
        long now = System.currentTimeMillis() / 1000;
        converter.setConverterId(null);
        converter.setConverterCreator(operator);
        converter.setConverterCreatetime(now);
        converter.setConverterUpdatetime(now);
        converter.setConverterStatus(STATUS_VALID);
        if (converter.getRodId() != null) {
            ensureRodValid(converter.getRodId());
        }
        this.save(converter);
        return converter.getConverterId();
    }

    @Override
    public void updateConverter(BizConverter in) {
        BizConverter db = getValidConverter(in.getConverterId());
        db.setConverterIp(in.getConverterIp());
        db.setConverterPort(in.getConverterPort());
        if (StringUtils.isNotBlank(in.getConverterSn())) {
            db.setConverterSn(in.getConverterSn());
        }
        db.setConverterUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(db);
    }

    @Override
    public void removeConverter(Long converterId) {
        BizConverter db = getValidConverter(converterId);
        db.setConverterStatus(converterId);
        db.setConverterUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(db);
    }

    @Override
    public PageUtils queryPage(ConverterForm form) {
        Page<BizConverter> page = new Page<>(form.getPageNum(), form.getPageSize());
        LambdaQueryWrapper<BizConverter> w = new LambdaQueryWrapper<BizConverter>()
                .eq(BizConverter::getConverterStatus, STATUS_VALID)
                .like(StringUtils.isNotBlank(form.getConverterSn()), BizConverter::getConverterSn, form.getConverterSn())
                .like(StringUtils.isNotBlank(form.getConverterIp()), BizConverter::getConverterIp, form.getConverterIp());
        if (Boolean.TRUE.equals(form.getBound())) {
            w.isNotNull(BizConverter::getRodId);
        } else if (Boolean.FALSE.equals(form.getBound())) {
            w.isNull(BizConverter::getRodId);
        }
        w.orderByDesc(BizConverter::getConverterId);
        Page<BizConverter> result = this.page(page, w);
        return new PageUtils(result);
    }

    @Override
    public void bind(Long converterId, Long rodId) {
        BizConverter db = getValidConverter(converterId);
        ensureRodValid(rodId);
        Integer exist = this.count(new LambdaQueryWrapper<BizConverter>()
                .eq(BizConverter::getRodId, rodId)
                .eq(BizConverter::getConverterStatus, STATUS_VALID)
                .ne(BizConverter::getConverterId, converterId));
        if (exist != null && exist > 0) {
            throw new RRException("该杆已被其他转换器绑定");
        }
        db.setRodId(rodId);
        db.setConverterUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(db);
    }

    @Override
    public void unbind(Long converterId) {
        getValidConverter(converterId);
        this.update(new LambdaUpdateWrapper<BizConverter>()
                .eq(BizConverter::getConverterId, converterId)
                .set(BizConverter::getRodId, null)
                .set(BizConverter::getConverterUpdatetime, System.currentTimeMillis() / 1000));
    }

    private BizConverter getValidConverter(Long converterId) {
        if (converterId == null) {
            throw new RRException("转换器ID不能为空");
        }
        BizConverter c = this.getById(converterId);
        if (c == null || !c.isValid()) {
            throw new RRException("转换器不存在或已删除");
        }
        return c;
    }

    private void ensureRodValid(Long rodId) {
        if (rodId == null) {
            throw new RRException("杆ID不能为空");
        }
        BizLiftingRod rod = rodMapper.selectById(rodId);
        if (rod == null || !rod.isValid()) {
            throw new RRException("目标杆不存在或已删除");
        }
    }
}
