package com.inteink.modules.biz.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@ApiModel(value = "LiftingStrategyForm", description = "升降策略分页查询参数")
public class LiftingStrategyForm {

    /**
     * 策略ID（精准查询/删除/修改用）
     */
    @ApiModelProperty(value = "策略ID（精准查询）", example = "10001")
    private Long strategyId;

    /**
     * 策略名称（模糊查询）
     */
    @ApiModelProperty(value = "策略名称（模糊查询）", example = "工作日早高峰升杆")
    private String strategyName;

    /**
     * 策略动作（参考 {@link StrategyActionEnum}，精准查询）
     */
    @ApiModelProperty(value = "策略动作（1-升杆/2-降杆）", example = "1")
    private Integer strategyAction;

    /**
     * 策略类型（参考 {@link StrategyTypeEnum}，精准查询）
     */
    @ApiModelProperty(value = "策略类型（1-每日/2-每周/3-每月/4-指定日期）", example = "1")
    private Integer strategyType;

    /**
     * 审核状态（参考 {@link StrategyCheckStateEnum}，精准查询）
     */
    @ApiModelProperty(value = "审核状态（0-待审核/1-通过/2-不通过）", example = "1")
    private Integer strategyCheckState;

    /**
     * 关联升降杆ID（筛选策略用）
     */
    @ApiModelProperty(value = "关联升降杆ID（筛选）", example = "20001")
    private Long rodId;

    /**
     * 页码（默认1，最小1）
     */
    @ApiModelProperty(value = "页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 每页条数（默认10，最小1，最大100）
     */
    @ApiModelProperty(value = "每页条数", example = "10")
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Integer pageSize = 10;

    // ---------------------- 内部字段（仅对内使用，对外隐藏）----------------------
    /**
     * 当前操作用户ID（内部字段，不对外展示）
     */
    @ApiModelProperty(hidden = true)
    private Long userId;

    /**
     * SQL过滤条件（如数据权限，内部字段，不对外展示）
     */
    @ApiModelProperty(hidden = true)
    private String sqlFilter;

    // ---------------------- 枚举定义（统一管理魔法数字）----------------------
    /**
     * 策略动作枚举
     */
    public enum StrategyActionEnum {
        LIFT_UP(1, "升杆"),
        LIFT_DOWN(2, "降杆");

        private final Integer code;
        private final String desc;

        StrategyActionEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 根据编码获取枚举
        public static StrategyActionEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (StrategyActionEnum enums : StrategyActionEnum.values()) {
                if (enums.code.equals(code)) {
                    return enums;
                }
            }
            return null;
        }

        // getter
        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 策略类型枚举
     */
    public enum StrategyTypeEnum {
        DAILY(1, "每日"),
        WEEKLY(2, "每周"),
        MONTHLY(3, "每月"),
        APPOINTED_DATE(4, "指定日期");

        private final Integer code;
        private final String desc;

        StrategyTypeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 根据编码获取枚举
        public static StrategyTypeEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (StrategyTypeEnum enums : StrategyTypeEnum.values()) {
                if (enums.code.equals(code)) {
                    return enums;
                }
            }
            return null;
        }

        // getter
        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 策略审核状态枚举
     */
    public enum StrategyCheckStateEnum {
        PENDING(0, "待审核"),
        PASS(1, "通过"),
        REJECT(2, "不通过");

        private final Integer code;
        private final String desc;

        StrategyCheckStateEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 根据编码获取枚举
        public static StrategyCheckStateEnum getByCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (StrategyCheckStateEnum enums : StrategyCheckStateEnum.values()) {
                if (enums.code.equals(code)) {
                    return enums;
                }
            }
            return null;
        }

        // getter
        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}