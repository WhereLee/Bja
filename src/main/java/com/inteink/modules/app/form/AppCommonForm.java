package com.inteink.modules.app.form;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Api
@Data
public class AppCommonForm {
    @ApiModelProperty(value = "当前页数 默认 1",position = 1)
    private String page;
    @ApiModelProperty(value = "每页显示数量 默认 10",position = 2)
    private String limit;

    @ApiModelProperty(value = "openid",position = 20)
    private String openid;

    @ApiModelProperty(value = "小程序对应用户id",position = 30)
    private Long userId;
}
