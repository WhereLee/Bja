package com.inteink.modules.biz.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 道闸杆展示对象：杆基本信息 + 状态文案 + 绑定的转换器信息。
 */
@ApiModel("杆展示对象")
@Data
public class LiftingRodVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("杆ID")
    private Long rodId;
    @ApiModelProperty("名称")
    private String rodName;
    @ApiModelProperty("安装位置")
    private String rodAddr;
    @ApiModelProperty("经度")
    private BigDecimal rodLongtitude;
    @ApiModelProperty("纬度")
    private BigDecimal rodLatitude;
    @ApiModelProperty("在线状态 0-离线 1-在线")
    private Integer rodOffline;
    @ApiModelProperty("在线状态文案")
    private String rodOfflineDesc;
    @ApiModelProperty("升降状态 0-默认 1-升 2-降")
    private Integer rodState;
    @ApiModelProperty("升降状态文案")
    private String rodStateDesc;
    @ApiModelProperty("备注")
    private String rodRemark;
    @ApiModelProperty("创建时间戳(秒)")
    private Long rodCreatetime;
    @ApiModelProperty("更新时间戳(秒)")
    private Long rodUpdatetime;

    @ApiModelProperty("是否已绑定转换器")
    private Boolean bound;
    @ApiModelProperty("绑定的转换器ID")
    private Long converterId;
    @ApiModelProperty("转换器SN")
    private String converterSn;
    @ApiModelProperty("转换器IP")
    private String converterIp;
    @ApiModelProperty("转换器端口")
    private Integer converterPort;
}
