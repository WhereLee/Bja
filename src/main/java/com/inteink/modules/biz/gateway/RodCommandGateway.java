package com.inteink.modules.biz.gateway;

/**
 * 道闸控制下发抽象：把一次“升/降”动作下发给物理设备（转换器/继电器）。
 * 本轮由 Mock 实现占位；接入真机协议时替换实现即可，业务层无需改动。
 */
public interface RodCommandGateway {

    /**
     * 下发控制指令。
     *
     * @param rodId  杆ID
     * @param action 动作 1-升 2-降
     * @return true-下发成功 false-下发失败
     */
    boolean send(Long rodId, Integer action);
}
