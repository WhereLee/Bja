package com.inteink.modules.biz.service.impl;

import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.vo.DashboardVO;
import com.inteink.modules.biz.model.vo.DeviceInfoVO;
import com.inteink.modules.biz.service.DeviceQueryService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 看板聚合纯逻辑单测：测 assemble（不依赖 MyBatis-Plus 的 wrapper）。
 */
class DashboardServiceImplTest {

    private BizLiftingRod rod(long id) {
        BizLiftingRod r = new BizLiftingRod();
        r.setRodId(id);
        r.setRodStatus(0L);
        r.setRodState(0);
        r.setRodOffline(1);
        return r;
    }

    private BizConverter conv(long id, long rodId) {
        BizConverter c = new BizConverter();
        c.setConverterId(id);
        c.setRodId(rodId);
        c.setConverterStatus(0L);
        c.setConverterSn("SN" + id);
        c.setConverterIp("127.0.0.1");
        c.setConverterPort(18080);
        return c;
    }

    @Test
    void 汇总在线离线未绑定() {
        DeviceQueryService probe = mock(DeviceQueryService.class);
        DashboardServiceImpl impl = new DashboardServiceImpl(
                mock(BizLiftingRodMapper.class), mock(BizConverterMapper.class), probe);

        List<BizLiftingRod> rods = Arrays.asList(rod(1), rod(2), rod(3));
        Map<Long, BizConverter> convByRod = new HashMap<>();
        convByRod.put(2L, conv(20, 2));
        convByRod.put(3L, conv(30, 3));

        DeviceInfoVO online = new DeviceInfoVO();
        online.setOnline(true);
        online.setState(1);
        online.setStateDesc("升");
        when(probe.probe(20L)).thenReturn(online);
        when(probe.probe(30L)).thenReturn(null); // 不可达 → 离线

        DashboardVO d = impl.assemble(rods, convByRod);

        assertEquals(3, d.getTotal());
        assertEquals(1, d.getOnlineCount());
        assertEquals(1, d.getOfflineCount());
        assertEquals(1, d.getUnboundCount());
    }
}
