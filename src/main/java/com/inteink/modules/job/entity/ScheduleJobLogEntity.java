/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.job.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@ApiModel("定时任务日志对象")
@Data
@TableName("schedule_job_log")
public class ScheduleJobLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 任务日志id
	 */
	@ApiModelProperty(value = "日志ID",position = 1)
	@TableId
	private Long logId;
	/**
	 * 任务id
	 */
	@ApiModelProperty(value = "任务ID",position = 2)
	private Long jobId;
	/**
	 * spring bean名称
	 */
	@ApiModelProperty(value = "Spring Bean名称",position = 3)
	private String jobBean;
	/**
	 * 任务名称
	 */
	@ApiModelProperty(value = "任务名称",position = 4)
	private String jobName;
	/**
	 * 执行状态    0：成功    1：失败
	 */
	@ApiModelProperty(value = "执行状态 0-成功 1-失败",position = 5)
	private Integer logState;
	/**
	 * 失败信息
	 */
	@ApiModelProperty(value = "失败信息",position = 6)
	private String logError;
	/**
	 * 执行时长 单位 毫秒
	 */
	@ApiModelProperty(value = "执行时长（毫秒）",position = 7)
	private Long logDuration;
	/**
	 * 创建时间戳，单位秒
	 */
	@ApiModelProperty(value = "创建时间戳（秒）",position = 8)
	private Long logCreatetime;
	
}
