package com.inteink.modules.biz.gateway.impl;

import com.inteink.modules.biz.gateway.RodCommandGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 道闸下发的模拟实现：不接真机，仅记录并返回成功。
 * 真实协议（网络继电器/厂商SDK）就绪后，新增一个实现类替换即可。
 */
@Slf4j
@Component
public class MockRodCommandGateway implements RodCommandGateway {

    @Override
    public boolean send(Long rodId, Integer action) {
        log.info("【道闸下发-模拟】rodId={}, action={}（未接入真实设备协议）", rodId, action);
        return true;
    }
}
