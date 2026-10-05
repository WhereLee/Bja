package com.inteink.modules.biz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("biz_lifting_rod")
public class BizLiftingRod {

    private static final long serialVersionUID = 1L;
    /**
     * 主键 自增
     */
    @TableId(value = "rod_id", type = IdType.AUTO) // 明确映射主键字段，自增策略
    private Long rodId;

    /**
     * 名称
     */
    @TableField("rod_name")
    private String rodName;

    /**
     * 安装位置
     */
    @TableField("rod_addr")
    private String rodAddr;

    /**
     * 经度
     */
    @TableField("rod_longtitude") // 注意表字段是longtitude（拼写可能是笔误，保持和表一致）
    private BigDecimal rodLongtitude;

    /**
     * 是否在线 0 不在线 1在线
     */
    @TableField("rod_offline")
    private Boolean rodOffline; // 用Boolean更直观，TINYINT(1)自动适配

    /**
     * 维度（应为“纬度”，表注释笔误，实体类注释修正）
     */
    @TableField("rod_latitude")
    private BigDecimal rodLatitude;

    /**
     * 备注
     */
    @TableField("rod_remark")
    private String rodRemark;

    /**
     * 升降状态 0-不显示（默认） 1-升 2-降
     */
    @TableField("rod_state")
    private Integer rodState;

    /**
     * 创建人
     */
    @TableField("rod_creator")
    private Long rodCreator;

    /**
     * 创建时间戳 秒
     */
    @TableField("rod_createtime")
    private Long rodCreatetime;

    /**
     * 更新时间戳 秒
     */
    @TableField("rod_updatetime")
    private Long rodUpdatetime;

    /**
     * 状态 0-有效 >0 无效 默认0
     */
    @TableField("rod_status")
    private Long rodStatus;

    /**
     * 升降状态枚举
     */
    public enum RodStateEnum {
        HIDE(0, "不显示"),
        UP(1, "升"),
        DOWN(2, "降");

        private final Integer code;
        private final String desc;

        RodStateEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 获取编码
        public Integer getCode() {
            return code;
        }

        // 获取描述
        public String getDesc() {
            return desc;
        }

        // 根据编码获取枚举（常用）
        public static RodStateEnum getByCode(Integer code) {
            for (RodStateEnum enumItem : RodStateEnum.values()) {
                if (enumItem.getCode().equals(code)) {
                    return enumItem;
                }
            }
            return HIDE; // 默认返回不显示
        }
    }

    /**
     * 在线状态枚举
     */
    public enum RodOfflineEnum {
        OFFLINE(0, "不在线"),
        ONLINE(1, "在线");

        private final Integer code;
        private final String desc;

        RodOfflineEnum(Integer code, String desc) {
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
