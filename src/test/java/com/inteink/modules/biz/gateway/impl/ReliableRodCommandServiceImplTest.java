package com.inteink.modules.biz.gateway.impl;

import com.inteink.modules.biz.gateway.DeviceResult;
import com.inteink.modules.biz.gateway.RodCommandGateway;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 可靠编排层单测：重试次数 / 不重试 / 去重跳过（用假网关，不触网）。
 */
class ReliableRodCommandServiceImplTest {

    private ReliableRodCommandServiceImpl svc(RodCommandGateway gw) {
        ReliableRodCommandServiceImpl s = new ReliableRodCommandServiceImpl(gw);
        ReflectionTestUtils.setField(s, "maxAttempts", 3);
        ReflectionTestUtils.setField(s, "baseBackoffMs", 1L);
        ReflectionTestUtils.setField(s, "dedupWindowMs", 3000L);
        return s;
    }

    @Test
    void 离线重试直到成功共发三次() {
        RodCommandGateway gw = mock(RodCommandGateway.class);
        when(gw.send(1L, 1)).thenReturn(DeviceResult.OFFLINE, DeviceResult.OFFLINE, DeviceResult.SUCCESS);
        assertEquals(DeviceResult.SUCCESS, svc(gw).execute(1L, 1));
        verify(gw, times(3)).send(1L, 1);
    }

    @Test
    void 设备拒绝不重试只发一次() {
        RodCommandGateway gw = mock(RodCommandGateway.class);
        when(gw.send(1L, 1)).thenReturn(DeviceResult.DEVICE_REJECT);
        assertEquals(DeviceResult.DEVICE_REJECT, svc(gw).execute(1L, 1));
        verify(gw, times(1)).send(1L, 1);
    }

    @Test
    void 成功后同动作在去重窗内跳过() {
        RodCommandGateway gw = mock(RodCommandGateway.class);
        when(gw.send(1L, 1)).thenReturn(DeviceResult.SUCCESS);
        ReliableRodCommandServiceImpl s = svc(gw);
        assertEquals(DeviceResult.SUCCESS, s.execute(1L, 1));
        assertEquals(DeviceResult.SKIPPED_DUPLICATE, s.execute(1L, 1));
        verify(gw, times(1)).send(1L, 1);
    }
}
