package com.inteink.modules.biz.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 看板单杆实时项：库内杆状态 + 绑定设备 + 设备实时探测态。
 */
@ApiModel("看板杆实时项")
@Data
public class RodLiveVO {
    @ApiModelProperty("杆ID")
    private Long rodId;
    @ApiModelProperty("杆名称")
    private String rodName;
    @ApiModelProperty("库内杆状态 0/1/2")
    private Integer rodState;
    @ApiModelProperty("状态描述")
    private String rodStateDesc;
    @ApiModelProperty("库内在线标记 0离线 1在线")
    private Integer rodOffline;
    @ApiModelProperty("是否绑定设备")
    private boolean bound;
    @ApiModelProperty("设备SN")
    private String deviceSn;
    @ApiModelProperty("设备地址 ip:port")
    private String deviceAddr;
    @ApiModelProperty("设备实时在线（探测可达）")
    private Boolean deviceOnline;
    @ApiModelProperty("设备实时状态")
    private Integer deviceState;
    @ApiModelProperty("设备状态描述")
    private String deviceStateDesc;
}
