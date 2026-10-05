/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录表单
 *
 * @author Mark sunlightcs@gmail.com
 */
@ApiModel("登录实体")
@Data
public class SysLoginForm {
    @ApiModelProperty(value = "登录名",position = 1)
    private String loginname;

    @ApiModelProperty(value = "登录密码",position = 10)
    private String password;

    //@ApiModelProperty(value = "手机号码",position = 30)
    //private String mobile;

    @ApiModelProperty(value = "验证码",position = 40)
    private String code;
    //private String captcha;
    //private String uuid;


}
