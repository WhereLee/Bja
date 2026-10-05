package com.inteink.modules.biz.model.dto;

import com.inteink.modules.biz.service.validator.StrategyDatesTypeMatch;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 修改升降策略请求参数
 */
@Data
@ApiModel(value = "修改升降策略请求参数", description = "修改升降策略的入参封装")
@StrategyDatesTypeMatch // 核心：类级动态校验，匹配你的格式规则
public class StrategyUpdateDTO {

    @NotNull(message = "策略ID不能为空")
    @ApiModelProperty(value = "策略ID", required = true, example = "55")
    private Long strategyId;

    @ApiModelProperty(value = "策略名称（可选）", example = "大门夜间降杆策略V2")
    private String strategyName;

    @Range(min = 1, max = 2, message = "动作类型只能是1（升杆）或2（降杆）")
    @ApiModelProperty(value = "动作类型：1-升杆/2-降杆（可选）", example = "2")
    private Integer strategyAction;

    @Range(min = 1, max = 4, message = "执行类型只能是1（每日）、2（每周）、3（每月）、4（指定日期）")
    @ApiModelProperty(value = "执行类型：1-每日/2-每周/3-每月/4-指定日期（可选）", example = "1")
    private Integer strategyType;

    @ApiModelProperty(value = "执行规则：1(每日)=HH:mm|2(每周)=1~7|3(每月)=1~31|4(指定日期)=YYYY-MM-DD HH:mm",
            example = "21:00") // 示例按类型适配
    private String strategyDates;

    @ApiModelProperty(value = "策略备注", example = "每晚21点降杆，早6点升杆")
    private String strategyRemark;

    @Pattern(regexp = "^\\d{2}:\\d{2},\\d{2}:\\d{2}$|^$", message = "执行时段格式错误，正确示例：21:00,06:00")
    @ApiModelProperty(value = "执行时段（可选，格式：begin,end，示例：21:00,06:00）", example = "21:00,06:00")
    private String detailTime;

    @Pattern(regexp = "^$|^\\d+(,\\d+)*$", message = "关联升降杆ID格式错误，多个用逗号分隔（如1,3,4）")
    @ApiModelProperty(value = "关联升降杆ID（可选，多个用逗号分隔，示例：1,3,4）", example = "1,3,4")
    private String rodIds;

    // 补充修改人ID字段
    @NotNull(message = "修改人ID不能为空")
    @ApiModelProperty(value = "修改人ID", required = true, example = "1001")
    private Long strategyUpdater;

}