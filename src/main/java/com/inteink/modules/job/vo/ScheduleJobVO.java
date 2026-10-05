package com.inteink.modules.job.vo;

import com.inteink.common.utils.StringUtils;
import com.inteink.common.validator.group.AddGroup;
import com.inteink.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel("定时任务VO")
@Data
public class ScheduleJobVO {
    @ApiModelProperty(value = "定时任务ID",position = 1)
    @NotNull(message = "定时任务主键不能为空",groups = {UpdateGroup.class})
    private Long jobId;

    @ApiModelProperty(value = "Bean名称",position = 2)
    @Size(max = 128,message = "Bean名称过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotBlank(message="Bean名称不能为空",groups = {AddGroup.class})
    private String jobBean;

    public void setJobBean(String jobBean) {
        this.jobBean = StringUtils.replaceBlank(jobBean);
    }

    @ApiModelProperty(value = "定时任务名称",position = 3)
    @Size(max = 128,message = "定时任务名称过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotBlank(message="定时任务名称不能为空",groups = {AddGroup.class})
    private String jobName;

    public void setJobName(String jobName) {
        this.jobName = StringUtils.replaceBlank(jobName);
    }

    @ApiModelProperty(value = "定时任务参数",position = 4)
    @Size(max = 2000,message = "定时任务参数过长",groups = {AddGroup.class,UpdateGroup.class})
    private String jobParams;

    public void setJobParams(String jobParams) {
        this.jobParams = StringUtils.replaceBlank(jobParams);
    }

    @ApiModelProperty(value = "Cron表达式",position = 5)
    @Size(max = 128,message = "Cron表达式过长",groups = {AddGroup.class,UpdateGroup.class})
    @NotBlank(message="Cron表达式不能为空",groups = {AddGroup.class})
    private String jobCron;

    /* Cron表达式不能去空格
    public void setJobCron(String jobCron) {
        this.jobCron = StringUtils.replaceBlank(jobCron);
    }*/

    @ApiModelProperty(value = "定时任务说明",position = 6)
    @Size(max = 256,message = "定时任务说明过长",groups = {AddGroup.class,UpdateGroup.class})
    private String jobComment;

    public void setJobComment(String jobComment) {
        this.jobComment = StringUtils.replaceBlank(jobComment);
    }
}
