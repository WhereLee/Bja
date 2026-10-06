package com.inteink.modules.biz.gateway;

/**
 * 一次设备下发的结果分类（为后续重试/幂等铺路）。
 * isRetryable：网络类（超时/离线）可重试；设备明确拒绝不可重试。
 */
public enum DeviceResult {
    /** 设备确认执行且回报状态一致 */
    SUCCESS(false),
    /** 请求超时（网络慢/无响应），可重试 */
    TIMEOUT(true),
    /** 连不上/设备离线，可重试 */
    OFFLINE(true),
    /** 设备明确拒绝（500/状态不符），不可重试 */
    DEVICE_REJECT(false),
    /** 重复/并发被去重跳过，未真正下发，不可重试 */
    SKIPPED_DUPLICATE(false);

    private final boolean retryable;

    DeviceResult(boolean retryable) {
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
