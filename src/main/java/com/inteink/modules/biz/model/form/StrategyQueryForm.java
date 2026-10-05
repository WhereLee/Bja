package com.inteink.modules.biz.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 升降策略 分页查询条件。
 */
@ApiModel("策略查询条件")
@Data
public class StrategyQueryForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("策略名称（模糊）")
    private String strategyName;
    @ApiModelProperty("类型")
    private Integer strategyType;
    @ApiModelProperty("审核状态 0-待审 1-通过 2-驳回")
    private Integer checkState;
    @ApiModelProperty("页码")
    private Integer pageNum = 1;
    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;
}
