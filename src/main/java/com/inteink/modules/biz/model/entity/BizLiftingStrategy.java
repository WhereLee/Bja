package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 升降策略：何时升/降哪些杆，带审核关口。
 */
@ApiModel("升降策略实体")
@Data
@TableName("biz_lifting_strategy")
public class BizLiftingStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("策略ID")
    @TableId(value = "strategy_id", type = IdType.AUTO)
    private Long strategyId;

    @ApiModelProperty("策略名称")
    private String strategyName;

    @ApiModelProperty("动作 1-升 2-降")
    private Integer strategyAction;

    @ApiModelProperty("类型 1-每日 2-每周 3-每月 4-指定日期")
    private Integer strategyType;

    @ApiModelProperty("具体日期（周几/几号/yyyy-MM-dd）")
    private String strategyDates;

    @ApiModelProperty("备注")
    private String strategyRemark;

    @ApiModelProperty("审核状态 0-待审核 1-通过 2-驳回")
    private Integer strategyCheckState;

    @ApiModelProperty("审核人")
    private Long strategyCheckUser;

    @ApiModelProperty("审核时间戳(秒)")
    private Long strategyCheckTime;

    @ApiModelProperty("创建人")
    private Long strategyCreator;

    @ApiModelProperty("创建时间戳(秒)")
    private Long strategyCreatetime;

    @ApiModelProperty("更新时间戳(秒)")
    private Long strategyUpdatetime;

    @ApiModelProperty("状态 0-有效 >0-无效(逻辑删除)")
    private Long strategyStatus;

    public boolean isValid() {
        return strategyStatus != null && strategyStatus == 0L;
    }
}
