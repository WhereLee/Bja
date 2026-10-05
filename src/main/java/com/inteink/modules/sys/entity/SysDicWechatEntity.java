package com.inteink.modules.sys.entity;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("微信小程序对象")
@Data
public class SysDicWechatEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "小程序appId",position = 20)
    private String wechatAppid;

    @ApiModelProperty(value = "小程序appSecret",position = 30)
    private String wechatAppsecret;

    public SysDicWechatEntity() {};

    public SysDicWechatEntity(String wechatAppid, String wechatAppsecret) {
        this.wechatAppid = wechatAppid;
        this.wechatAppsecret = wechatAppsecret;
    }

    /**
     * 校验
     * @return true：通过 false：微信小程序参数未配置
     */
    public Boolean check() {
        return (StringUtils.isNotBlank(wechatAppid) && StringUtils.isNotBlank(wechatAppsecret));
    }
}
