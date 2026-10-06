package com.inteink.modules.biz.gateway;

import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 下发结果分类 + isRetryable 的纯逻辑单测。
 */
class DeviceResultClassifierTest {

    @Test
    void 响应码分类() {
        assertEquals(DeviceResult.OFFLINE, DeviceResultClassifier.classifyResponse(503, null, 1));
        assertEquals(DeviceResult.DEVICE_REJECT, DeviceResultClassifier.classifyResponse(500, null, 1));
        assertEquals(DeviceResult.SUCCESS, DeviceResultClassifier.classifyResponse(200, 1, 1));
        assertEquals(DeviceResult.DEVICE_REJECT, DeviceResultClassifier.classifyResponse(200, 2, 1));
        assertEquals(DeviceResult.DEVICE_REJECT, DeviceResultClassifier.classifyResponse(200, null, 1));
    }

    @Test
    void 异常分类() {
        assertEquals(DeviceResult.TIMEOUT, DeviceResultClassifier.classifyException(new SocketTimeoutException("t")));
        assertEquals(DeviceResult.OFFLINE, DeviceResultClassifier.classifyException(new ConnectException("c")));
        assertEquals(DeviceResult.OFFLINE, DeviceResultClassifier.classifyException(new RuntimeException("x")));
    }

    @Test
    void 可重试标记() {
        assertTrue(DeviceResult.TIMEOUT.isRetryable());
        assertTrue(DeviceResult.OFFLINE.isRetryable());
        assertFalse(DeviceResult.SUCCESS.isRetryable());
        assertFalse(DeviceResult.DEVICE_REJECT.isRetryable());
        assertFalse(DeviceResult.SKIPPED_DUPLICATE.isRetryable());
    }
}
