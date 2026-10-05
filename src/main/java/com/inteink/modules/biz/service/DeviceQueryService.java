package com.inteink.modules.biz.service;

import com.inteink.modules.biz.model.vo.DeviceInfoVO;

/**
 * 设备运行态查询：向转换器按其地址实时拉取状态，判在线/离线。
 */
public interface DeviceQueryService {

    /**
     * 探测某转换器（设备）的实时状态。
     *
     * @param converterId 转换器ID
     * @return 设备视图；转换器不存在时返回 null；不可达时 online=false、state=null
     */
    DeviceInfoVO probe(Long converterId);
}
