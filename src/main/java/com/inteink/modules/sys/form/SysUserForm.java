package com.inteink.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户查询对象")
@Data
public class SysUserForm extends CommonForm{
    @ApiModelProperty(value = "用户姓名",position = 3)
    private String userName;
    @ApiModelProperty(value = "电话号码",position = 8)
    private String userPhone;
}
