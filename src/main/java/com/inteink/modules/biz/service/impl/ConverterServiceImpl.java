package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.form.ConverterForm;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.service.lifting.ConverterService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 转换器Service实现类（核心校验+CRUD）
 */
@Service
public class ConverterServiceImpl extends ServiceImpl<BizConverterMapper, BizConverter> implements ConverterService {

    @Autowired
    private LiftingRodService liftingRodService; // 关联升降杆的Service

    // 1. 分页查询（后续配合XML实现关联查询，先写基础版）
    @Override
    public PageUtils queryPage(ConverterForm form) {
        IPage<BizConverter> page = new Page<>(form.getPageNum(), form.getPageSize());
        page = baseMapper.queryConverterPage(page, form);
        return new PageUtils(page);
    }

    // 2. 新增转换器（核心：硬件参数+关联升降杆校验）
    @Override
    public boolean saveConverter(BizConverter converter) {
        // 校验1：关联的升降杆是否存在
        BizLiftingRod rod = liftingRodService.getById(converter.getRodId());
        if (rod == null) {
            throw new RuntimeException("关联的升降杆ID=" + converter.getRodId() + "不存在");
        }
        // 校验2：IP格式是否合法
        if (!converter.checkIpFormat()) {
            throw new RuntimeException("IP地址" + converter.getConverterIp() + "格式不合法");
        }
        // 校验3：端口是否合法
        if (!converter.checkPortValid()) {
            throw new RuntimeException("端口" + converter.getConverterPort() + "不合法（范围0-65535）");
        }
        // 补充默认值
        converter.setConverterCreatetime(System.currentTimeMillis() / 1000);
        converter.setConverterUpdatetime(System.currentTimeMillis() / 1000);
        converter.setConverterStatus(0L); // 默认有效
        return this.save(converter);
    }

    @Override
    public boolean updateConverter(BizConverter converter) {
        // 1. 校验转换器是否存在
        BizConverter existConverter = this.getById(converter.getConverterId());
        if (existConverter == null) {
            throw new RuntimeException("转换器ID=" + converter.getConverterId() + "不存在");
        }

        // 2. 校验：改rodId时检查升降杆是否存在（原逻辑已支持）
        if (converter.getRodId() != null && !existConverter.getRodId().equals(converter.getRodId())) {
            BizLiftingRod rod = liftingRodService.getById(converter.getRodId());
            if (rod == null) {
                throw new RuntimeException("关联的升降杆ID=" + converter.getRodId() + "不存在");
            }
        }

        // 3. 校验：改IP时检查格式（原逻辑已支持）
        if (converter.getConverterIp() != null && !existConverter.getConverterIp().equals(converter.getConverterIp())) {
            if (!converter.checkIpFormat()) {
                throw new RuntimeException("IP地址" + converter.getConverterIp() + "格式不合法");
            }
        }

        // 4. 校验：改端口时检查合法性（原逻辑已支持）
        if (converter.getConverterPort() != null && !existConverter.getConverterPort().equals(converter.getConverterPort())) {
            if (!converter.checkPortValid()) {
                throw new RuntimeException("端口" + converter.getConverterPort() + "不合法（范围0-65535）");
            }
        }

        // 5. 补充更新时间
        converter.setConverterUpdatetime(System.currentTimeMillis() / 1000);
        // 6. 执行修改（自动更新所有非null字段：端口、IP、rod_id）
        return this.updateById(converter);
    }

    // 新增：查询单条转换器（按ID）
    @Override
    public BizConverter getConverterById(Long converterId) {
        // 直接查询数据库（包含所有状态：有效/无效）
        return this.getById(converterId);
    }
}