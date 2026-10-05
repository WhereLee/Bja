package com.inteink.modules.sys.entity;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("短信对象")
@Data
public class SysDicSmsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "阿里云APPKey",position = 20)
    private String aliyunAppkey;

    @ApiModelProperty(value = "阿里云APPServer",position = 30)
    private String aliyunAppserve;

    @ApiModelProperty(value = "阿里云SignName",position = 40)
    private String aliyunSignname;

    @ApiModelProperty(value = "阿里云TemplateCode",position = 50)
    private String aliyunTemplatecode;

    @ApiModelProperty(value = "阿里云TemplateCode-msg",position = 52)
    private String aliyunTemplatecodeMsg;

    public SysDicSmsEntity() {};

    public SysDicSmsEntity(String aliyunAppkey, String aliyunAppserve, String aliyunSignname, String aliyunTemplatecode, String aliyunTemplatecodeMsg) {
        this.aliyunAppkey = aliyunAppkey;
        this.aliyunAppserve = aliyunAppserve;
        this.aliyunSignname = aliyunSignname;
        this.aliyunTemplatecode = aliyunTemplatecode;
        this.aliyunTemplatecodeMsg = aliyunTemplatecodeMsg;
    }

    /**
     * 校验参数
     * @return true：成功  false：
     */
    public Boolean check() {
        return (StringUtils.isNotBlank(aliyunAppkey) && StringUtils.isNotBlank(aliyunAppserve) && StringUtils.isNotBlank(aliyunSignname) && StringUtils.isNotBlank(aliyunTemplatecode));
    }

    /**
     * 校验参数-发送消息
     * @return true：成功  false：
     */
    public Boolean checkMsg() {
        return (StringUtils.isNotBlank(aliyunAppkey) && StringUtils.isNotBlank(aliyunAppserve) && StringUtils.isNotBlank(aliyunSignname) && StringUtils.isNotBlank(aliyunTemplatecodeMsg));
    }
}
