package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@ApiModel("企业微信VO")
@Data
public class SysDicWechatVO {
    @ApiModelProperty(value = "小程序appId",position = 30)
    @Size(max = 2048,message = "小程序appId过长")
    private String wechatAppid;

    public void setWechatAppid(String wechatAppid) {
        this.wechatAppid = StringUtils.replaceBlank(wechatAppid);
    }

    @ApiModelProperty(value = "小程序appSecret",position = 40)
    @Size(max = 2048,message = "小程序appSecret过长")
    private String wechatAppsecret;

    public void setWechatAppsecret(String wechatAppsecret) {
        this.wechatAppsecret = StringUtils.replaceBlank(wechatAppsecret);
    }
}
