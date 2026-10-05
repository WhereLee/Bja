package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import com.inteink.common.validator.BlankOrPattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@ApiModel("企业微信VO")
@Data
public class SysDicQyweixinVO {
    @ApiModelProperty(value = "企业微信-企业ID",position = 30)
    @Size(max = 2048,message = "企业微信-企业ID过长")
    private String qyweixinCorpid;

    public void setQyweixinCorpid(String qyweixinCorpid) {
        this.qyweixinCorpid = StringUtils.replaceBlank(qyweixinCorpid);
    }

    @ApiModelProperty(value = "企业微信-应用的ID",position = 40)
    @Size(max = 2048,message = "企业微信-应用ID过长")
    private String qyweixinAgentid;

    public void setQyweixinAgentid(String qyweixinAgentid) {
        this.qyweixinAgentid = StringUtils.replaceBlank(qyweixinAgentid);
    }

    @ApiModelProperty(value = "企业微信-应用的凭证",position = 50)
    @Size(max = 2048,message = "企业微信-应用的凭证过长")
    private String qyweixinCorsecret;

    public void setQyweixinCorsecret(String qyweixinCorsecret) {
        this.qyweixinCorsecret = StringUtils.replaceBlank(qyweixinCorsecret);
    }
}
