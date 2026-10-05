package com.inteink.modules.sys.vo;

import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("重置密码VO")
@Data
public class SysPasswordVO {
    @ApiModelProperty(value = "用户ID",position = 1)
    @NotNull(message = "用户主键不能为空")
    private Long userId;        //用户ID

    @ApiModelProperty(value = "用户密码",position = 2)
    @NotBlank(message="密码不能为空")
    private String password;    //密码

}
