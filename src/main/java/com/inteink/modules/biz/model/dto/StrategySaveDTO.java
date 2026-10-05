package com.inteink.modules.biz.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 新增升降策略请求DTO
 */
@Data
@ApiModel(value = "新增升降策略请求参数", description = "新增升降策略的入参封装")
public class StrategySaveDTO {

    @NotBlank(message = "策略名称不能为空")
    @ApiModelProperty(value = "策略名称", required = true, example = "大门夜间降杆策略")
    private String strategyName;

    @NotNull(message = "动作类型不能为空")
    @ApiModelProperty(value = "动作类型：1-升杆/2-降杆", required = true, example = "2")
    private Integer strategyAction;

    @NotNull(message = "执行类型不能为空")
    @ApiModelProperty(value = "执行类型：1-每日/2-每周/3-每月/4-指定日期", required = true, example = "1")
    private Integer strategyType;

    @NotBlank(message = "执行规则不能为空")
    @ApiModelProperty(value = "执行规则（每日传时间/每周传周几/每月传日期，示例：20:00）", required = true, example = "20:00")
    private String strategyDates;

    @ApiModelProperty(value = "策略备注（可选）", example = "每晚20点降杆，早6点升杆")
    private String strategyRemark;

    @NotNull(message = "创建人ID不能为空")
    @ApiModelProperty(value = "创建人ID", required = true, example = "1001")
    private Long strategyCreator;

    @NotBlank(message = "执行时段不能为空")
    @ApiModelProperty(value = "执行时间（支持两种格式：单时间=HH:mm（仅触发单次动作，如降杆）、双时间=HH:mm,HH:mm（触发开始+结束动作，如降杆+升杆），示例1：18:56，示例2：18:56,18:58）", required = true, example = "18:56")
    private String detailTime;

    @ApiModelProperty(value = "关联升降杆ID（多个用逗号分隔，手动指定，示例：1,2,3）", example = "1,2")
    private String rodIds;
}