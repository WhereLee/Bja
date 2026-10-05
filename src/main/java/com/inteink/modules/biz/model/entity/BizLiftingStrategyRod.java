package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 策略-杆 关联（多对多）。
 */
@ApiModel("策略杆关联实体")
@Data
@TableName("biz_lifting_strategy_rod")
public class BizLiftingStrategyRod implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("策略ID")
    private Long strategyId;

    @ApiModelProperty("杆ID")
    private Long rodId;
}
