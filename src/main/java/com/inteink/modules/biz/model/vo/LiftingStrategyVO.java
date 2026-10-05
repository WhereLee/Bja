package com.inteink.modules.biz.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 升降策略展示对象。
 */
@ApiModel("策略展示对象")
@Data
public class LiftingStrategyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("策略ID")
    private Long strategyId;
    @ApiModelProperty("策略名称")
    private String strategyName;
    @ApiModelProperty("动作 1-升 2-降")
    private Integer strategyAction;
    @ApiModelProperty("动作文案")
    private String strategyActionDesc;
    @ApiModelProperty("类型")
    private Integer strategyType;
    @ApiModelProperty("类型文案")
    private String strategyTypeDesc;
    @ApiModelProperty("具体日期")
    private String strategyDates;
    @ApiModelProperty("备注")
    private String strategyRemark;
    @ApiModelProperty("审核状态 0-待审 1-通过 2-驳回")
    private Integer strategyCheckState;
    @ApiModelProperty("审核状态文案")
    private String strategyCheckStateDesc;
    @ApiModelProperty("开始时间 HH:mm")
    private String detailBegin;
    @ApiModelProperty("结束时间 HH:mm")
    private String detailEnd;
    @ApiModelProperty("绑定的杆ID列表")
    private List<Long> rodIds;
    @ApiModelProperty("创建时间戳(秒)")
    private Long strategyCreatetime;
    @ApiModelProperty("更新时间戳(秒)")
    private Long strategyUpdatetime;
}
