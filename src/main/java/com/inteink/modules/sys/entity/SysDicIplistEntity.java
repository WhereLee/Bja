package com.inteink.modules.sys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("IP黑白名单对象")
@Data
public class SysDicIplistEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "白名单列表 英文逗号分隔",position = 20)
    private String whiteList;

    @ApiModelProperty(value = "黑名单列表 英文逗号分隔",position = 30)
    private String blackList;

    public SysDicIplistEntity() {};

    public SysDicIplistEntity(String whiteList, String blackList) {
        this.whiteList = whiteList;
        this.blackList = blackList;
    }
}
