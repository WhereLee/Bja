package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.enums.RodStateEnum;
import com.inteink.modules.biz.model.vo.DashboardVO;
import com.inteink.modules.biz.model.vo.DeviceInfoVO;
import com.inteink.modules.biz.model.vo.RodLiveVO;
import com.inteink.modules.biz.service.DashboardService;
import com.inteink.modules.biz.service.DeviceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 看板聚合实现：取有效杆 → 关联绑定转换器 → 逐台探测设备实时态 → 汇总在线/离线。
 * 只读、不改状态；探测为逐台有界超时，适合中小规模。
 * 纯聚合逻辑拆到 assemble(...)，便于不依赖 MyBatis-Plus 的单元测试。
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final long STATUS_VALID = 0L;

    private final BizLiftingRodMapper rodMapper;
    private final BizConverterMapper converterMapper;
    private final DeviceQueryService deviceQueryService;

    @Override
    public DashboardVO dashboard() {
        List<BizLiftingRod> rods = rodMapper.selectList(new LambdaQueryWrapper<BizLiftingRod>()
                .eq(BizLiftingRod::getRodStatus, STATUS_VALID)
                .orderByDesc(BizLiftingRod::getRodId));
        return assemble(rods, loadConverters(rods));
    }

    /** 纯聚合：由杆 + (rodId→converter) 组装看板；probe 探测在线/离线。 */
    DashboardVO assemble(List<BizLiftingRod> rods, Map<Long, BizConverter> convByRod) {
        List<RodLiveVO> items = new ArrayList<>();
        int online = 0;
        int offline = 0;
        int unbound = 0;
        for (BizLiftingRod rod : rods) {
            RodLiveVO vo = new RodLiveVO();
            vo.setRodId(rod.getRodId());
            vo.setRodName(rod.getRodName());
            vo.setRodState(rod.getRodState());
            vo.setRodStateDesc(RodStateEnum.descOf(rod.getRodState()));
            vo.setRodOffline(rod.getRodOffline());

            BizConverter conv = convByRod.get(rod.getRodId());
            if (conv == null) {
                vo.setBound(false);
                unbound++;
            } else {
                vo.setBound(true);
                vo.setDeviceSn(conv.getConverterSn());
                vo.setDeviceAddr(conv.getConverterIp() + ":" + conv.getConverterPort());
                DeviceInfoVO probe = deviceQueryService.probe(conv.getConverterId());
                boolean devOnline = probe != null && Boolean.TRUE.equals(probe.getOnline());
                vo.setDeviceOnline(devOnline);
                if (devOnline) {
                    vo.setDeviceState(probe.getState());
                    vo.setDeviceStateDesc(probe.getStateDesc());
                    online++;
                } else {
                    offline++;
                }
            }
            items.add(vo);
        }
        DashboardVO dash = new DashboardVO();
        dash.setTotal(rods.size());
        dash.setOnlineCount(online);
        dash.setOfflineCount(offline);
        dash.setUnboundCount(unbound);
        dash.setRods(items);
        return dash;
    }

    private Map<Long, BizConverter> loadConverters(List<BizLiftingRod> rods) {
        if (rods.isEmpty()) {
            return new HashMap<>();
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
}
