package com.inteink.modules.biz.gateway;

/**
 * 道闸控制下发抽象：把一次“升/降”动作下发给物理设备（转换器/继电器）。
 * Mock / HTTP(虚拟设备) / 真机协议 各一个实现，业务层无需改动。
 */
public interface RodCommandGateway {

    /**
     * 下发控制指令。
     *
     * @param rodId  杆ID
     * @param action 动作 1-升 2-降
     * @return 下发结果分类（成功/超时/离线/设备拒绝）
     */
    DeviceResult send(Long rodId, Integer action);
}
