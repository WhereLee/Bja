package com.inteink.modules.biz.service;

import com.inteink.modules.biz.model.vo.DashboardVO;

/**
 * 看板聚合：只读地汇总所有杆及其绑定设备的实时状态。
 */
public interface DashboardService {

    DashboardVO dashboard();
}
