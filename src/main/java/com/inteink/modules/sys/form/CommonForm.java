package com.inteink.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@ApiModel("公共查询条件")
@Data
public class CommonForm {

    @ApiModelProperty(value = "当前页数 默认 1",position = 1)
    private String page;
    @ApiModelProperty(value = "每页显示数量 默认 10",position = 2)
    private String limit;
    private String sqlFilter;//数据权限限制-前端不必关注
}
