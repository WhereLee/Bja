package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 杆动作日志：每一次抬/落杆的留痕（手动或自动）。
 */
@ApiModel("杆动作日志实体")
@Data
@TableName("biz_lifting_rod_log")
public class BizLiftingRodLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @ApiModelProperty("杆ID")
    private Long rodId;

    @ApiModelProperty("类型 1-手动 2-自动")
    private Integer logType;

    @ApiModelProperty("动作 1-升 2-降")
    private Integer logAction;

    @ApiModelProperty("来源策略ID（自动时有值，可空）")
    private Long strategyId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("结果 1-成功 2-失败")
    private Integer logResult;

    @ApiModelProperty("操作时间戳(秒)")
    private Long logOperateTime;
}
