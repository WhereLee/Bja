package com.inteink.modules.biz.gateway;

/**
 * 可靠下发编排层：在传输网关之上做 并发/去重守卫 + 有限次退避重试。
 * 网关只发一次，重试与幂等在这里。
 */
public interface ReliableRodCommandService {

    /**
     * 对某杆下发一次动作，带重试与去重。
     *
     * @return 最终下发结果（SUCCESS / TIMEOUT / OFFLINE / DEVICE_REJECT / SKIPPED_DUPLICATE）
     */
    DeviceResult execute(Long rodId, Integer action);
}
