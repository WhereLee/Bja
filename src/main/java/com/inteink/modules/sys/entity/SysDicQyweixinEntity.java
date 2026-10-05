package com.inteink.modules.sys.entity;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("企业微信对象")
@Data
public class SysDicQyweixinEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业微信-企业ID",position = 20)
    private String qyweixinCorpid;

    @ApiModelProperty(value = "企业微信-应用的ID",position = 30)
    private String qyweixinAgentid;

    @ApiModelProperty(value = "企业微信-应用的凭证",position = 40)
    private String qyweixinCorsecret;

    public SysDicQyweixinEntity() {};

    public SysDicQyweixinEntity(String qyweixinCorpid, String qyweixinAgentid, String qyweixinCorsecret) {
        this.qyweixinCorpid = qyweixinCorpid;
        this.qyweixinAgentid = qyweixinAgentid;
        this.qyweixinCorsecret = qyweixinCorsecret;
    }

    /**
     * 校验
     * @return true：通过 false：企业微信参数未配置
     */
    public Boolean check() {
        return (StringUtils.isNotBlank(qyweixinCorpid) && StringUtils.isNotBlank(qyweixinAgentid) && StringUtils.isNotBlank(qyweixinCorsecret));
    }
}
