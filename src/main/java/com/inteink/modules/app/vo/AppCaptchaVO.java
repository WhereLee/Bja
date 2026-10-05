package com.inteink.modules.app.vo;

import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel("验证码VO")
@Data
public class AppCaptchaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "手机号",position = 1)
    @Size(max = 128,message = "手机号过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotBlank(message="手机号不能为空",groups = {AddGroup.class})
    private String mobile;

    @ApiModelProperty(value = "验证码",position = 10)
    @NotNull(message="验证码不能为空",groups = {AddGroup.class})
    private Integer code;
}
