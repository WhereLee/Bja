package com.inteink.modules.biz.gateway.impl;

import com.inteink.modules.biz.gateway.DeviceResult;
import com.inteink.modules.biz.gateway.RodCommandGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 道闸下发的模拟实现：不接真机，直接返回成功。
 * 真实协议就绪后新增实现替换即可。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "biz.device", name = "mode", havingValue = "mock", matchIfMissing = true)
public class MockRodCommandGateway implements RodCommandGateway {

    @Override
    public DeviceResult send(Long rodId, Integer action) {
        log.info("【道闸下发-模拟】rodId={}, action={}（未接入真实设备协议）", rodId, action);
        return DeviceResult.SUCCESS;
    }
}
