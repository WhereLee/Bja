package com.inteink.modules.sys.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("系统日志查询条件")
@Data
public class SysLogForm extends CommonForm{
    @ApiModelProperty(value = "日志类型 1-WEB端 2-APP端",position = 10)
    private Integer logType;

    @ApiModelProperty(value = "模块",position = 10)
    private String logModule;       //模块

    @ApiModelProperty(value = "功能",position = 20)
    private String logFunc;         //功能

    @ApiModelProperty(value = "执行结果 0-成功 1-失败",position = 23)
    private Integer logState;       //执行结果 0-成功 1-失败

    @ApiModelProperty(value = "参数",position = 26)
    private String logParams;       //参数

    @ApiModelProperty(value = "操作人",position = 30)
    private String logCreatorName;  //操作人

    @ApiModelProperty(value = "OPENID",position = 40)
    private String logOpenid;

    @ApiModelProperty(value = "昵称",position = 50)
    private String logNickname;

    @ApiModelProperty(value = "手机",position = 60)
    private String logMobile;

    @ApiModelProperty(value = "起始时间戳，单位秒",position = 70)
    private Long starttime;         //操作起始时间

    @ApiModelProperty(value = "截止时间戳，单位秒",position = 80)
    private Long endtime;           //操作截止时间
}
