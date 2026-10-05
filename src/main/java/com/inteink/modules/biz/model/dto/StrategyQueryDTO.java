package com.inteink.modules.biz.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 策略分页查询请求DTO
 */
@Data
@ApiModel(value = "策略分页查询请求参数", description = "策略分页查询的入参封装")
public class StrategyQueryDTO {

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页大小", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "策略名称（模糊查询）", example = "夜间降杆")
    private String strategyName;

    @ApiModelProperty(value = "动作类型：1-升杆/2-降杆", example = "2")
    private Integer strategyAction;

    @ApiModelProperty(value = "审核状态：0-待审核/1-通过/2-驳回", example = "1")
    private Integer checkState;

    @ApiModelProperty(value = "关联杆ID（筛选绑定该杆的策略）", example = "1")
    private Long rodId;
}