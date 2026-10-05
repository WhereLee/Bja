package com.inteink.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("角色查询实体")
@Data
public class SysRoleForm extends CommonForm{
    @ApiModelProperty(value = "角色名称",position = 3)
    private String roleName;    //角色名称
    @ApiModelProperty(value = "角色描述",position = 4)
    private String roleComment; //角色描述
}
