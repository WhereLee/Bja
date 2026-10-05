package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@ApiModel("短信VO")
@Data
public class SysDicSmsVO {
    @ApiModelProperty(value = "阿里云APPKey",position = 30)
    @Size(max = 2048,message = "阿里云APPKey过长")
    private String aliyunAppkey;

    public void setAliyunAppkey(String aliyunAppkey) {
        this.aliyunAppkey = StringUtils.replaceBlank(aliyunAppkey);
    }

    @ApiModelProperty(value = "阿里云APPServer",position = 30)
    @Size(max = 2048,message = "阿里云APPServer过长")
    private String aliyunAppserve;

    public void setAliyunAppserve(String aliyunAppserve) {
        this.aliyunAppserve = StringUtils.replaceBlank(aliyunAppserve);
    }

    @ApiModelProperty(value = "阿里云短信签名",position = 40)
    @Size(max = 2048,message = "阿里云短信签名过长")
    private String aliyunSignname;

    public void setAliyunSignname(String aliyunSignname) {
        this.aliyunSignname = StringUtils.replaceBlank(aliyunSignname);
    }

    @ApiModelProperty(value = "阿里云短信模板-验证码",position = 50)
    @Size(max = 2048,message = "阿里云短信模板-验证码过长")
    private String aliyunTemplatecode;

    public void setAliyunTemplatecode(String aliyunTemplatecode) {
        this.aliyunTemplatecode = StringUtils.replaceBlank(aliyunTemplatecode);
    }

    @ApiModelProperty(value = "阿里云短信模板-消息",position = 52)
    @Size(max = 2048,message = "阿里云短信模板-消息g过长")
    private String aliyunTemplatecodeMsg;

    public void setAliyunTemplatecodeMsg(String aliyunTemplatecodeMsg) {
        this.aliyunTemplatecodeMsg = StringUtils.replaceBlank(aliyunTemplatecodeMsg);
    }
}
