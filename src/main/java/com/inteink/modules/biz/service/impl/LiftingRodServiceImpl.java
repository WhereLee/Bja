package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.exception.RRException;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.StringUtils;
import com.inteink.modules.biz.gateway.RodCommandGateway;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.enums.RodActionEnum;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.model.enums.RodStateEnum;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import com.inteink.modules.biz.service.LiftingRodService;
import com.inteink.modules.biz.service.RodOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiftingRodServiceImpl extends ServiceImpl<BizLiftingRodMapper, BizLiftingRod>
        implements LiftingRodService {

    private static final long STATUS_VALID = 0L;

    private final RodCommandGateway rodCommandGateway;
    private final RodOperationLogService rodOperationLogService;
    private final BizConverterMapper converterMapper;

    @Override
    public Long saveRod(BizLiftingRod rod, Long operator) {
        if (StringUtils.isBlank(rod.getRodName())) {
            throw new RRException("杆名称不能为空");
        }
        Integer dup = this.count(new LambdaQueryWrapper<BizLiftingRod>()
                .eq(BizLiftingRod::getRodName, rod.getRodName())
                .eq(BizLiftingRod::getRodStatus, STATUS_VALID));
        if (dup != null && dup > 0) {
            throw new RRException("同名升降杆已存在");
        }
        long now = System.currentTimeMillis() / 1000;
        rod.setRodId(null);
        rod.setRodCreator(operator);
        rod.setRodCreatetime(now);
        rod.setRodUpdatetime(now);
        rod.setRodStatus(STATUS_VALID);
        if (rod.getRodState() == null) {
            rod.setRodState(RodStateEnum.DEFAULT.getCode());
        }
        if (rod.getRodOffline() == null) {
            rod.setRodOffline(0);
        }
        this.save(rod);
        return rod.getRodId();
    }

    @Override
    public void updateRod(BizLiftingRod in) {
        BizLiftingRod db = getValidRod(in.getRodId());
        if (StringUtils.isNotBlank(in.getRodName())) {
            db.setRodName(in.getRodName());
        }
        db.setRodAddr(in.getRodAddr());
        db.setRodLongtitude(in.getRodLongtitude());
        db.setRodLatitude(in.getRodLatitude());
        db.setRodRemark(in.getRodRemark());
        db.setRodUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(db);
    }

    @Override
    public void removeRod(Long rodId) {
        BizLiftingRod db = getValidRod(rodId);
        db.setRodStatus(rodId);
        db.setRodUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(db);
    }

    @Override
    public PageUtils queryPage(LiftingRodForm form) {
        Page<BizLiftingRod> page = new Page<>(form.getPageNum(), form.getPageSize());
        LambdaQueryWrapper<BizLiftingRod> w = new LambdaQueryWrapper<BizLiftingRod>()
                .eq(BizLiftingRod::getRodStatus, STATUS_VALID)
                .like(StringUtils.isNotBlank(form.getRodName()), BizLiftingRod::getRodName, form.getRodName())
                .eq(form.getRodOffline() != null, BizLiftingRod::getRodOffline, form.getRodOffline())
                .eq(form.getRodState() != null, BizLiftingRod::getRodState, form.getRodState())
                .orderByDesc(BizLiftingRod::getRodId);
        Page<BizLiftingRod> result = this.page(page, w);
        List<BizLiftingRod> records = result.getRecords();
        Map<Long, BizConverter> convByRod = loadConverters(records);
        List<LiftingRodVO> vos = records.stream()
                .map(r -> toVO(r, convByRod.get(r.getRodId())))
                .collect(Collectors.toList());
        return new PageUtils(vos, (int) result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public LiftingRodVO detail(Long rodId) {
        BizLiftingRod rod = getValidRod(rodId);
        BizConverter conv = converterMapper.selectOne(new LambdaQueryWrapper<BizConverter>()
                .eq(BizConverter::getRodId, rodId)
                .eq(BizConverter::getConverterStatus, STATUS_VALID)
                .last("LIMIT 1"));
        return toVO(rod, conv);
    }

    @Override
    public void manualOperate(Long rodId, Integer action, Long operator) {
        BizLiftingRod rod = getValidRod(rodId);
        if (!RodActionEnum.isValid(action)) {
            throw new RRException("非法动作，只能 1-升 2-降");
        }
        boolean ok = rodCommandGateway.send(rodId, action);
        if (ok) {
            rod.setRodState(action);
            rod.setRodUpdatetime(System.currentTimeMillis() / 1000);
            this.updateById(rod);
        }
        rodOperationLogService.record(rodId, action, RodLogTypeEnum.MANUAL, null, ok);
        if (!ok) {
            throw new RRException("道闸控制下发失败");
        }
    }

    private BizLiftingRod getValidRod(Long rodId) {
        if (rodId == null) {
            throw new RRException("杆ID不能为空");
        }
        BizLiftingRod rod = this.getById(rodId);
        if (rod == null || !rod.isValid()) {
            throw new RRException("升降杆不存在或已删除");
        }
        return rod;
    }

    private Map<Long, BizConverter> loadConverters(List<BizLiftingRod> rods) {
        if (rods == null || rods.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = rods.stream().map(BizLiftingRod::getRodId).collect(Collectors.toList());
        List<BizConverter> convs = converterMapper.selectList(new LambdaQueryWrapper<BizConverter>()
                .in(BizConverter::getRodId, ids)
                .eq(BizConverter::getConverterStatus, STATUS_VALID));
        Map<Long, BizConverter> map = new HashMap<>();
        for (BizConverter c : convs) {
            if (c.getRodId() != null) {
                map.putIfAbsent(c.getRodId(), c);
            }
        }
        return map;
    }

    private LiftingRodVO toVO(BizLiftingRod r, BizConverter c) {
        LiftingRodVO vo = new LiftingRodVO();
        vo.setRodId(r.getRodId());
        vo.setRodName(r.getRodName());
        vo.setRodAddr(r.getRodAddr());
        vo.setRodLongtitude(r.getRodLongtitude());
        vo.setRodLatitude(r.getRodLatitude());
        vo.setRodOffline(r.getRodOffline());
        vo.setRodOfflineDesc(r.getRodOffline() != null && r.getRodOffline() == 1 ? "在线" : "离线");
        vo.setRodState(r.getRodState());
        vo.setRodStateDesc(RodStateEnum.descOf(r.getRodState()));
        vo.setRodRemark(r.getRodRemark());
        vo.setRodCreatetime(r.getRodCreatetime());
        vo.setRodUpdatetime(r.getRodUpdatetime());
        if (c != null) {
            vo.setBound(true);
            vo.setConverterId(c.getConverterId());
            vo.setConverterSn(c.getConverterSn());
            vo.setConverterIp(c.getConverterIp());
            vo.setConverterPort(c.getConverterPort());
        } else {
            vo.setBound(false);
        }
        return vo;
    }
}
