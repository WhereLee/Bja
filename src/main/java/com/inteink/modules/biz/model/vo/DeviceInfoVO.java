package com.inteink.modules.biz.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备（转换器）运行态视图：静态台账（sn/地址）+ 实时查询到的状态与在线性。
 */
@ApiModel("设备状态视图")
@Data
public class DeviceInfoVO {
    @ApiModelProperty("转换器ID")
    private Long converterId;
    @ApiModelProperty("设备编号 SN")
    private String sn;
    @ApiModelProperty("IP地址")
    private String ip;
    @ApiModelProperty("端口")
    private Integer port;
    @ApiModelProperty("是否在线（能连上并返回状态）")
    private Boolean online;
    @ApiModelProperty("设备回报的当前状态 0/1/2")
    private Integer state;
    @ApiModelProperty("状态描述")
    private String stateDesc;
}
