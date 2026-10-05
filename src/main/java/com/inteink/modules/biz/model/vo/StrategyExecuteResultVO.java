package com.inteink.modules.biz.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 升降策略执行结果VO
 */
@Data
@ApiModel(value = "升降策略执行结果", description = "策略执行后的汇总信息")
public class StrategyExecuteResultVO {

    @ApiModelProperty(value = "策略ID")
    private Long strategyId;

    @ApiModelProperty(value = "策略名称")
    private String strategyName;

    @ApiModelProperty(value = "执行动作编码：1-升杆/2-降杆")
    private Integer actionCode;

    @ApiModelProperty(value = "执行动作描述")
    private String actionDesc;

    @ApiModelProperty(value = "绑定杆总数")
    private Integer totalRodCount;

    @ApiModelProperty(value = "成功执行数量")
    private Integer successCount;

    @ApiModelProperty(value = "失败执行数量")
    private Integer failCount;

    @ApiModelProperty(value = "跳过执行数量（杆不存在/失效）")
    private Integer skipCount;

    @ApiModelProperty(value = "执行汇总信息")
    private String executeMsg;

    @ApiModelProperty(value = "执行的杆ID")
    private Long rodId;

    @ApiModelProperty(value = "执行的杆名称")
    private String rodName;
}