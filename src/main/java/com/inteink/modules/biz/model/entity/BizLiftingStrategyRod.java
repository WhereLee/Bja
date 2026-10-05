package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("biz_lifting_strategy_rod") // 精准映射数据表名
public class BizLiftingStrategyRod implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 升降策略ID（关联biz_lifting_strategy表的strategy_id）
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 升降杆ID（关联biz_lifting_rod表的rod_id）
     */
    @TableField("rod_id")
    private Long rodId;

    // ========== 扩展：常用查询条件封装（可选） ==========
    /**
     * 构建关联查询条件（根据策略ID查关联的杆ID）
     * @param strategyId 策略ID
     * @return 关联实体
     */
    public static BizLiftingStrategyRod buildByStrategyId(Long strategyId) {
        BizLiftingStrategyRod rod = new BizLiftingStrategyRod();
        rod.setStrategyId(strategyId);
        return rod;
    }

    /**
     * 构建关联查询条件（根据杆ID查关联的策略ID）
     * @param rodId 升降杆ID
     * @return 关联实体
     */
    public static BizLiftingStrategyRod buildByRodId(Long rodId) {
        BizLiftingStrategyRod rod = new BizLiftingStrategyRod();
        rod.setRodId(rodId);
        return rod;
    }
}