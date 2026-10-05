package com.inteink.modules.job.form;

import com.inteink.modules.sys.form.CommonForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("定时任务日志查询条件")
@Data
public class ScheduleJobLogForm extends CommonForm {
    @ApiModelProperty(value = "任务bean",position = 10)
    private String jobBean;     //任务bean

    @ApiModelProperty(value = "任务名称",position = 20)
    private String jobName;     //任务名称

    @ApiModelProperty(value = "执行状态 0：成功    1：失败",position = 30)
    private Integer logState;   //执行状态 0：成功    1：失败

    @ApiModelProperty(value = "起始时间戳，单位秒",position = 70)
    private Long starttime;     //操作起始时间戳 单位秒

    @ApiModelProperty(value = "截止时间戳，单位秒",position = 80)
    private Long endtime;       //操作截止时间戳 单位秒
}
