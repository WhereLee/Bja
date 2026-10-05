package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("biz_lifting_strategy_log") // 精准映射数据表名
public class BizLiftingStrategyLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 自增
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 升降策略ID（关联biz_lifting_strategy表的strategy_id）
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 类型 1-创建 2-修改 3-审核通过 4-审核不通过
     */
    @TableField("log_type")
    private Integer logType;

    /**
     * 备注
     */
    @TableField("log_remark")
    private String logRemark;

    /**
     * 操作人（关联用户表ID，修正表字段拼写：log_opeartor → 应为log_operator）
     */
    @TableField("log_opeartor") // 保持和表字段拼写一致，同时注释提醒笔误
    private Long logOperator;

    /**
     * 操作时间戳 秒
     */
    @TableField("log_operate_time")
    private Long logOperateTime;

    // ========== 枚举封装==========
    /**
     * 策略日志类型枚举（创建/修改/审核通过/审核不通过）
     */
    public enum LogTypeEnum {
        CREATE(1, "创建"),
        UPDATE(2, "修改"),
        CHECK_PASS(3, "审核通过"),
        CHECK_REJECT(4, "审核不通过");

        private final Integer code;
        private final String desc;

        LogTypeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        /**
         * 根据编码获取枚举
         * @param code 日志类型编码
         * @return 对应的枚举，无匹配则返回null
         */
        public static LogTypeEnum getByCode(Integer code) {
            for (LogTypeEnum enumItem : LogTypeEnum.values()) {
                if (enumItem.getCode().equals(code)) {
                    return enumItem;
                }
            }
            return null;
        }
    }
}