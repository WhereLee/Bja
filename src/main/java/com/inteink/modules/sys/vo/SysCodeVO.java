package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel("验证码VO")
@Data
public class SysCodeVO {
    @ApiModelProperty(value = "获取类型 1-根据登录名发送 2-根据手机号码或企业微信ID发送",position = 1)
    @Min(value = 1, message = "获取类型错误")
    @Max(value = 2, message = "获取类型错误")
    @NotNull(message = "验证码获取类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "登录名",position = 10)
    private String loginname;

    public void setLoginname(String loginname) {
        this.loginname = StringUtils.replaceBlank(loginname);
    }

    @ApiModelProperty(value = "手机号码",position = 30)
    private String mobile;

    public void setMobile(String mobile) {
        this.mobile = StringUtils.replaceBlank(mobile);
    }

    @ApiModelProperty(value = "企业微信ID",position = 40)
    private String qyweixinId;

    public void setQyweixinId(String qyweixinId) {
        this.qyweixinId = StringUtils.replaceBlank(qyweixinId);
    }
}
