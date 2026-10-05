package com.inteink.modules.biz.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 升降杆返回VO
 */
@Data
public class LiftingRodVO {
    @ApiModelProperty(value = "杆ID")
    private Long rodId;

    @ApiModelProperty(value = "杆名称")
    private String rodName;

    @ApiModelProperty(value = "杆地址")
    private String rodAddr;

    @ApiModelProperty(value = "杆离线状态值：0-在线/1-离线")
    private Integer rodOffline;

    @ApiModelProperty(value = "杆离线状态描述（中文）")
    private String rodOfflineDesc;

    @ApiModelProperty(value = "杆运行状态值：0-正常/>0-异常")
    private Integer rodStatus;

    @ApiModelProperty(value = "杆运行状态描述（中文）")
    private String rodStatusDesc;
}