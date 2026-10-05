package com.inteink.modules.biz.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 道闸杆 列表/分页查询条件。
 */
@ApiModel("杆查询条件")
@Data
public class LiftingRodForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("名称（模糊）")
    private String rodName;

    @ApiModelProperty("在线状态 0-离线 1-在线")
    private Integer rodOffline;

    @ApiModelProperty("升降状态 0-默认 1-升 2-降")
    private Integer rodState;

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;
}
