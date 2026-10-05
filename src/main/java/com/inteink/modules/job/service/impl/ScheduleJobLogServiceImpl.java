/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.exception.RRException;
import com.inteink.common.utils.*;
import com.inteink.modules.job.form.ScheduleJobLogForm;
import com.inteink.modules.job.service.ScheduleJobLogService;
import com.inteink.modules.job.dao.ScheduleJobLogDao;
import com.inteink.modules.job.entity.ScheduleJobLogEntity;
import org.springframework.stereotype.Service;

@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogDao, ScheduleJobLogEntity> implements ScheduleJobLogService {

	/**
	 * 查询定时任务执行日志-分页
	 * @param form
	 * @return
	 */
	@Override
	public PageUtils queryPage(ScheduleJobLogForm form) {
		QueryWrapper<ScheduleJobLogEntity> wrapper = new QueryWrapper<ScheduleJobLogEntity>()
				.like(StringUtils.isNotBlank(form.getJobName()), "job_name", form.getJobName())
				.like(StringUtils.isNotBlank(form.getJobBean()), "job_bean", form.getJobBean())
				.eq(form.getLogState() != null, "log_state", form.getLogState())
				.ge(form.getStarttime() != null, "log_createtime", form.getStarttime())
				.le(form.getEndtime() != null, "log_createtime", form.getEndtime());

		IPage<ScheduleJobLogEntity> page = this.page(
			new Query<ScheduleJobLogEntity>().getPage(new MapUtils()
					.put(Constant.PAGE,form.getPage()).put(Constant.LIMIT,form.getLimit())
					.put(Constant.ORDER_FIELD,"log_id").put(Constant.ORDER,"desc")),
			wrapper
		);

		return new PageUtils(page);
	}

	/**
	 * 根据ID 查询定时任务执行日志
	 * @param logId
	 * @return
	 */
	@Override
	public ScheduleJobLogEntity getInfo(Long logId) {
		//定时任务执行日志
		ScheduleJobLogEntity log = this.getById(logId);

		if (log == null || log.getLogId() == null) {
			throw new RRException("该日志信息不存在或已删除");
		}

		return log;
	}
}
