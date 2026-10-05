package com.inteink.modules.sys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("登录返回实体")
@Data
public class SysLoginEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "token",position = 1)
    private String token;

    @ApiModelProperty(value = "token有效期",position = 10)
    private Integer expire;

    @ApiModelProperty(value = "登录用户的userId",position = 20)
    private Long userId;

    @ApiModelProperty(value = "登录用户所拥有的角色Id列表",position = 30)
    private List<Long> roleIdList;

    private String msg;

    public SysLoginEntity() {}

    public SysLoginEntity(String token, Integer expire) {
        this.token = token;
        this.expire = expire;
        this.msg = "登录成功";
    }
}
