package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("biz_lifting_rod_log") // 精准映射数据表名
public class BizLiftingRodLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 自增
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 升降杆ID（关联biz_lifting_rod表的rod_id）
     */
    @TableField("rod_id")
    private Long rodId;

    /**
     * 类型 1-手动 2-自动
     */
    @TableField("log_type")
    private Integer logType;

    /**
     * 动作 1-升 2-降
     */
    @TableField("log_action")
    private Integer logAction;

    /**
     * 升降策略ID（关联biz_lifting_strategy表的strategy_id，自动操作时赋值）
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 操作结果 1-成功 2-失败
     */
    @TableField("log_result")
    private Integer logResult;

    /**
     * 操作时间戳 秒
     */
    @TableField("log_operate_time")
    private Long logOperateTime;

    // ========== 枚举封装（优化状态字段，避免魔法值） ==========
    /**
     * 操作类型枚举（手动/自动）
     */
    public enum LogTypeEnum {
        MANUAL(1, "手动"),
        AUTO(2, "自动");

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

        // 根据编码获取枚举
        public static LogTypeEnum getByCode(Integer code) {
            for (LogTypeEnum enumItem : LogTypeEnum.values()) {
                if (enumItem.getCode().equals(code)) {
                    return enumItem;
                }
            }
            return null;
        }
    }

    /**
     * 操作动作枚举（升/降）
     */
    public enum LogActionEnum {
        UP(1, "升"),
        DOWN(2, "降");

        private final Integer code;
        private final String desc;

        LogActionEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        // 作用：根据传入的编码（1/2），返回对应的枚举对象
        public static LogActionEnum getByCode(Integer code) {
            // 先判空，避免空指针
            if (code == null) {
                return null;
            }
            // 遍历所有枚举值，匹配编码
            for (LogActionEnum enumItem : LogActionEnum.values()) {
                if (enumItem.getCode().equals(code)) {
                    return enumItem;
                }
            }
            // 没有匹配的编码，返回null
            return null;
        }
    }

    /**
     * 操作结果枚举（成功/失败）
     */
    public enum LogResultEnum {
        SUCCESS(1, "成功"),
        FAIL(2, "失败");

        private final Integer code;
        private final String desc;

        LogResultEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}