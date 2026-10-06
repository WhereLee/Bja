package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.enums.RodStateEnum;
import com.inteink.modules.biz.model.vo.DashboardVO;
import com.inteink.modules.biz.model.vo.RodLiveVO;
import com.inteink.modules.biz.service.DashboardService;
import com.inteink.modules.biz.service.DeviceStateCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 看板聚合：读设备态快照缓存（由 DeviceStateCache 后台定时刷新），请求路径不再现场探测设备。
 * 只读、不改状态。
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final long STATUS_VALID = 0L;

    private final BizLiftingRodMapper rodMapper;
    private final BizConverterMapper converterMapper;
    private final DeviceStateCache deviceStateCache;

    @Override
    public DashboardVO dashboard() {
        List<BizLiftingRod> rods = rodMapper.selectList(new LambdaQueryWrapper<BizLiftingRod>()
                .eq(BizLiftingRod::getRodStatus, STATUS_VALID)
                .orderByDesc(BizLiftingRod::getRodId));
        return assemble(rods, loadConverters(rods), deviceStateCache.snapshot());
    }

    /** 纯聚合：由杆 + (rodId→converter) + (rodId→设备快照) 组装看板。 */
    DashboardVO assemble(List<BizLiftingRod> rods, Map<Long, BizConverter> convByRod,
                         Map<Long, DeviceStateCache.DevSnapshot> snapshots) {
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
                DeviceStateCache.DevSnapshot snap = snapshots.get(rod.getRodId());
                boolean devOnline = snap != null && snap.online();
                vo.setDeviceOnline(devOnline);
                if (devOnline) {
                    vo.setDeviceState(snap.state());
                    vo.setDeviceStateDesc(snap.stateDesc());
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
