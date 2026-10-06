package com.inteink.modules.biz.gateway;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

/**
 * 下发结果分类（纯函数，便于单测）：把 HTTP 状态/设备回报态/异常映射成 DeviceResult。
 */
public final class DeviceResultClassifier {

    private DeviceResultClassifier() {
    }

    /**
     * 依据响应码与设备回报态分类。
     *
     * @param statusCode  HTTP 状态码
     * @param deviceState 200 时设备回报的 state（否则可为 null）
     * @param action      期望动作
     */
    public static DeviceResult classifyResponse(int statusCode, Integer deviceState, Integer action) {
        if (statusCode == 503) {
            return DeviceResult.OFFLINE;
        }
        if (statusCode != 200) {
            return DeviceResult.DEVICE_REJECT;
        }
        return (deviceState != null && deviceState.equals(action))
                ? DeviceResult.SUCCESS : DeviceResult.DEVICE_REJECT;
    }

    /** 依据异常类型分类：读超时→TIMEOUT；连不上→OFFLINE。 */
    public static DeviceResult classifyException(Throwable e) {
        if (e instanceof HttpTimeoutException) {
            return DeviceResult.TIMEOUT;
        }
        if (e instanceof ConnectException) {
            return DeviceResult.OFFLINE;
        }
        if (e instanceof SocketTimeoutException) {
            return DeviceResult.TIMEOUT;
        }
        return DeviceResult.OFFLINE;
    }
}
