package com.inteink.modules.sys.form;

import com.inteink.modules.sys.form.CommonForm;
import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * 查询数据对象
 * @author wll
 * @email 
 * @date 2021-02-20 15:06:27
 */
@ApiModel("小程序用户查询对象")
@Data
public class SysUserWechatForm extends CommonForm {
    @ApiModelProperty(value = "OPENID",position = 10)
    private String wechatOpenid;

    @ApiModelProperty(value = "昵称",position = 20)
    private String wechatNickname;

    @ApiModelProperty(value = "手机",position = 40)
    private String wechatMobile;

    @ApiModelProperty(value = "管理员用户名",position = 80)
    private String userName;
}
