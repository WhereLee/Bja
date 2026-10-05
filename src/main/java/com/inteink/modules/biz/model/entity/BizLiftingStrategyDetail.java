package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 升降策略时间窗：一次抬/落的开始与结束时间（HH:mm）。
 */
@ApiModel("策略时间窗实体")
@Data
@TableName("biz_lifting_strategy_detail")
public class BizLiftingStrategyDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("明细ID")
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;

    @ApiModelProperty("策略ID")
    private Long strategyId;

    @ApiModelProperty("开始时间 HH:mm")
    private String detailBegin;

    @ApiModelProperty("结束时间 HH:mm")
    private String detailEnd;
}
