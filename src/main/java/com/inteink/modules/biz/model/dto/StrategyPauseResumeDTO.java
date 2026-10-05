package com.inteink.modules.biz.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 策略暂停/恢复操作DTO（共用）
 */
@Data
@ApiModel(value = "策略暂停/恢复参数", description = "策略暂停、恢复操作的通用参数（仅含操作人）")
public class StrategyPauseResumeDTO {

    @ApiModelProperty(value = "操作人ID", required = true)
    @NotNull(message = "操作人ID不能为空")
    @Range(min = 1, message = "操作人ID必须为正整数")
    private Long operator;
}