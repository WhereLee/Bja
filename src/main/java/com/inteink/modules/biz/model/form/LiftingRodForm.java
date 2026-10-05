package com.inteink.modules.biz.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "升降杆查询表单", description = "升降杆操作/日志查询参数")
public class LiftingRodForm {
    @ApiModelProperty(value = "升降杆ID", example = "1")
    private Long rodId;

    @ApiModelProperty(value = "操作类型：1-手动，2-自动", example = "1")
    private Integer logType;

    @ApiModelProperty(value = "操作动作：1-升，2-降", example = "1")
    private Integer logAction;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize = 10;

    // 以下为公司项目通用参数（对齐SysLogForm）
    @ApiModelProperty(hidden = true) // 隐藏，无需前端传
    private Long userId;

    @ApiModelProperty(hidden = true)
    private String sqlFilter;
}