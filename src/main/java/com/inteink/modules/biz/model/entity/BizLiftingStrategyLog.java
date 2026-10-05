package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 策略审核/操作日志（生命周期审计）。
 */
@ApiModel("策略审核日志实体")
@Data
@TableName("biz_lifting_strategy_log")
public class BizLiftingStrategyLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @ApiModelProperty("策略ID")
    private Long strategyId;

    @ApiModelProperty("类型 1-创建 2-修改 3-审核通过 4-审核驳回")
    private Integer logType;

    @ApiModelProperty("备注")
    private String logRemark;

    @ApiModelProperty("操作人")
    @TableField("log_opeartor")
    private Long logOperator;

    @ApiModelProperty("操作时间戳(秒)")
    private Long logOperateTime;
}
