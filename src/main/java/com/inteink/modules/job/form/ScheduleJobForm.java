package com.inteink.modules.job.form;

import com.inteink.modules.sys.form.CommonForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("定时任务查询对象")
@Data
public class ScheduleJobForm extends CommonForm {
    @ApiModelProperty(value = "Spring Bean名称",position = 2)
    private String jobBean;     //Bean名称
    @ApiModelProperty(value = "任务名称",position = 3)
    private String jobName;     //任务名称
    @ApiModelProperty(value = "任务状态 0-正常 1-暂停",position = 6)
    private Integer jobState;   //任务状态 0：正常  1：暂停
}
