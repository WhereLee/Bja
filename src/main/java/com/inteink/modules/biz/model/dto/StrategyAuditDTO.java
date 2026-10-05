package com.inteink.modules.biz.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 策略审核请求DTO
 */
@Data
@ApiModel(value = "策略审核请求参数", description = "策略审核的入参封装")
public class StrategyAuditDTO {

    @NotNull(message = "策略ID不能为空")
    @ApiModelProperty(value = "策略ID", required = true, example = "55")
    private Long strategyId;

    @NotNull(message = "审核状态不能为空")
    @ApiModelProperty(value = "审核状态：1-通过/2-驳回", required = true, example = "1")
    private Integer checkState;

    @NotNull(message = "审核人ID不能为空")
    @ApiModelProperty(value = "审核人ID", required = true, example = "1002")
    private Long operatorId;

    @ApiModelProperty(value = "审核驳回原因（仅checkState=2时必填，示例：执行时间与现有策略冲突）", example = "执行时间设置不合理，与现有策略时段冲突")
    private String reason;

    // 校验审核状态合法性（非注解方式，配合RuleEngine）
    public static final List<Integer> VALID_CHECK_STATES = Arrays.asList(1, 2);
}