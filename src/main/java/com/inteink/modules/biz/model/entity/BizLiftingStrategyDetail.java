package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 升降策略时间明细实体类
 * 对应表：biz_lifting_strategy_detail
 * @author 实习开发
 * @date 2025-11-27
 */
@Data
@TableName("biz_lifting_strategy_detail") // 精准映射数据表名
public class BizLiftingStrategyDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 自增
     */
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;

    /**
     * 升降策略ID（关联biz_lifting_strategy表的strategy_id）
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 开始时间（格式示例：05:00）
     */
    @TableField("detail_begin")
    private String detailBegin;

    /**
     * 截止时间（格式示例：17:00）
     */
    @TableField("detail_end")
    private String detailEnd;

    // ========== 扩展说明（可选） ==========
    // 若业务中需要校验时间格式，可添加自定义方法（比如校验是否为HH:mm格式）
    /**
     * 校验开始/截止时间是否为HH:mm格式（辅助方法，可选）
     * @return 格式是否合法
     */
    public boolean checkTimeFormat() {
        // 简单的正则校验HH:mm格式（00:00~23:59）
        String timeRegex = "^([01]\\d|2[0-3]):[0-5]\\d$";
        return (detailBegin != null && detailBegin.matches(timeRegex))
                && (detailEnd != null && detailEnd.matches(timeRegex));
    }
}