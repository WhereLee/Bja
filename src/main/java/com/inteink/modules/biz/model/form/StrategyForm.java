package com.inteink.modules.biz.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 升降策略 新增/修改入参（含单个时间窗 + 绑定杆列表）。
 */
@ApiModel("策略新增/修改入参")
@Data
public class StrategyForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("策略ID（修改时必填）")
    private Long strategyId;
    @ApiModelProperty("策略名称")
    private String strategyName;
    @ApiModelProperty("动作 1-升 2-降")
    private Integer strategyAction;
    @ApiModelProperty("类型 1-每日 2-每周 3-每月 4-指定日期")
    private Integer strategyType;
    @ApiModelProperty("具体日期（周几如2,3,5 / 几号如1,15 / yyyy-MM-dd）")
    private String strategyDates;
    @ApiModelProperty("备注")
    private String strategyRemark;
    @ApiModelProperty("开始时间 HH:mm")
    private String detailBegin;
    @ApiModelProperty("结束时间 HH:mm（可空，则只生成开始任务）")
    private String detailEnd;
    @ApiModelProperty("绑定的杆ID列表")
    private List<Long> rodIds;
}
