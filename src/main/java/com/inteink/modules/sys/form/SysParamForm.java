package com.inteink.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("参数查询对象")
@Data
public class SysParamForm extends CommonForm {
    @ApiModelProperty(value = "参数名称",position = 2)
    private String paramName;
}
