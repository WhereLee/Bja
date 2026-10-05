package com.inteink.modules.biz.model.vo;

import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 升降策略统一返回VO
 */
@Data
@ApiModel(value = "升降策略返回结果", description = "升降策略接口的统一返回数据格式")
public class StrategyResponseVO {

    @ApiModelProperty(value = "策略ID")
    private Long strategyId;

    @ApiModelProperty(value = "策略名称")
    private String strategyName;

    @ApiModelProperty(value = "动作类型：1-升杆/2-降杆")
    private Integer strategyAction;

    @ApiModelProperty(value = "执行类型：1-每日/2-每周/3-每月/4-指定日期")
    private Integer strategyType;

    @ApiModelProperty(value = "执行规则")
    private String strategyDates;

    @ApiModelProperty(value = "策略备注")
    private String strategyRemark;

    @ApiModelProperty(value = "审核状态值：0-待审核/1-审核通过/2-审核驳回")
    private Integer strategyCheckState;

    @ApiModelProperty(value = "审核状态描述（中文）")
    private String strategyCheckStateDesc;

    @ApiModelProperty(value = "策略状态值：0-有效/1-删除")
    private Long strategyStatus;

    @ApiModelProperty(value = "策略状态描述（中文）")
    private String strategyStatusDesc;

    @ApiModelProperty(value = "绑定的升降杆完整信息列表")
    private List<LiftingRodVO> rodList;

    @ApiModelProperty(value = "执行时段明细")
    private List<BizLiftingStrategyDetail> detailList;

    // 快速构建返回VO（适配不同场景）
    public static StrategyResponseVO buildBaseVO(BizLiftingStrategy strategy) {
        StrategyResponseVO vo = new StrategyResponseVO();
        vo.setStrategyId(strategy.getStrategyId());
        vo.setStrategyName(strategy.getStrategyName());
        vo.setStrategyAction(strategy.getStrategyAction());
        vo.setStrategyType(strategy.getStrategyType());
        vo.setStrategyDates(strategy.getStrategyDates());
        vo.setStrategyRemark(strategy.getStrategyRemark());
        vo.setStrategyCheckState(strategy.getStrategyCheckState());
        vo.setStrategyStatus(strategy.getStrategyStatus());
        vo.setRodList(new ArrayList<>());
        vo.setDetailList(new ArrayList<>());
        return vo;
    }
}