package com.inteink.modules.sys.vo;

import com.inteink.common.utils.StringUtils;
import com.inteink.common.validator.BlankOrPattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@ApiModel("IP黑白名单VO")
@Data
public class SysDicIplistVO {
    @ApiModelProperty(value = "白名单列表 英文逗号分隔",position = 30)
    @BlankOrPattern(regexp = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])(\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)){3}(,(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])(\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)){3})*$"
            ,message = "白名单列表格式错误")
    @Size(max = 2048,message = "白名单列表过长")
    private String whiteList;

    public void setWhiteList(String whiteList) {
        this.whiteList = StringUtils.replaceBlank(whiteList);
    }

    @ApiModelProperty(value = "黑名单列表 英文逗号分隔",position = 30)
    @BlankOrPattern(regexp = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])(\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)){3}(,(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])(\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)){3})*$"
            ,message = "黑名单列表格式错误")
    @Size(max = 2048,message = "黑名单列表过长")
    private String blackList;

    public void setBlackList(String blackList) {
        this.blackList = StringUtils.replaceBlank(blackList);
    }
}
