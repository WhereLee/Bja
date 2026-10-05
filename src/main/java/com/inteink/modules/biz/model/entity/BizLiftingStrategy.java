package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("biz_lifting_strategy") // 精准映射数据表名
public class BizLiftingStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 自增
     */
    @TableId(value = "strategy_id", type = IdType.AUTO)
    private Long strategyId;

    /**
     * 策略名称
     */
    @TableField("strategy_name")
    private String strategyName;

    /**
     * 动作 1-升 2-降
     */
    @TableField("strategy_action")
    private Integer strategyAction;

    /**
     * 类型 1-每日 2-每周 3-每月 4-指定日期（一次性）
     */
    @TableField("strategy_type")
    private Integer strategyType;

    /**
     * 具体日期 （周一、周三；1号、15号；2024年4月12日等）
     */
    @TableField("strategy_dates")
    private String strategyDates;

    /**
     * 备注
     */
    @TableField("strategy_remark")
    private String strategyRemark;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核不通过
     */
    @TableField("strategy_check_state")
    private Integer strategyCheckState;

    /**
     * 审核人（关联用户表ID）
     */
    @TableField("strategy_check_user")
    private Long strategyCheckUser;

    /**
     * 审核时间戳 秒
     */
    @TableField("strategy_check_time")
    private Long strategyCheckTime;

    /**
     * 创建人（关联用户表ID）
     */
    @TableField("strategy_creator")
    private Long strategyCreator;

    /**
     * 创建时间戳 秒
     */
    @TableField("strategy_createtime")
    private Long strategyCreatetime;

    /**
     * 更新时间戳 秒
     */
    @TableField("strategy_updatetime")
    private Long strategyUpdatetime;

    /**
     * 状态 0-有效 >0 无效 默认0
     */
    @TableField("strategy_status")
    private Long strategyStatus;

}