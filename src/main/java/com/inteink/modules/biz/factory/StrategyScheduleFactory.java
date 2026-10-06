package com.inteink.modules.biz.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.common.exception.RRException;
import com.inteink.common.utils.StringUtils;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.enums.StrategyNodeTypeEnum;
import com.inteink.modules.biz.util.StrategyCronUtil;
import com.inteink.modules.job.entity.ScheduleJobEntity;
import com.inteink.modules.job.service.ScheduleJobService;
import com.inteink.modules.job.utils.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TimeZone;

/**
 * 策略 ↔ 定时任务工厂：审核通过时按时间窗生成成对任务(BEGIN/END)，
 * 并支持按策略反查其任务进行增删/暂停/恢复。
 * 关联不加 schedule_job 列：jobBean = liftingStrategyTask_{strategyId}_{begin|end}。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StrategyScheduleFactory {

    /** 与任务体 bean 名一致（ScheduleJob 反射时取 jobBean.split("[-_]")[0]） */
    public static final String TASK_BEAN = "liftingStrategyTask";
    private static final Integer ACTION_LIFT = 1;
    private static final Integer ACTION_LOWER = 2;
    private static final long JOB_STATUS_VALID = 0L;
    private static final int JOB_STATE_NORMAL = 0;

    /** biz 策略任务固定时区，避免随 JVM 默认时区漂移 */
    private static final TimeZone BIZ_TIMEZONE = TimeZone.getTimeZone("Asia/Shanghai");

    private final ScheduleJobService scheduleJobService;
    private final Scheduler scheduler;

    /**
     * 为审核通过的策略生成定时任务（先清旧的，保证幂等）。
     */
    public void createJobs(BizLiftingStrategy strategy, BizLiftingStrategyDetail detail) {
        removeJobs(strategy.getStrategyId());
        if (detail == null) {
            throw new RRException("策略缺少时间窗明细，无法生成定时任务");
        }
        String beginCron = StrategyCronUtil.build(strategy, detail.getDetailBegin());
        if (StringUtils.isBlank(beginCron)) {
            throw new RRException("策略开始时间无效，无法生成定时任务");
        }
        createOne(strategy, StrategyNodeTypeEnum.BEGIN, strategy.getStrategyAction(), beginCron);

        if (StringUtils.isNotBlank(detail.getDetailEnd())) {
            String endCron = StrategyCronUtil.build(strategy, detail.getDetailEnd());
            if (StringUtils.isNotBlank(endCron)) {
                createOne(strategy, StrategyNodeTypeEnum.END, reverse(strategy.getStrategyAction()), endCron);
            }
        }
        log.info("策略定时任务生成完成，strategyId={}", strategy.getStrategyId());
    }

    private void createOne(BizLiftingStrategy strategy, StrategyNodeTypeEnum node, Integer action, String cron) {
        long now = System.currentTimeMillis() / 1000;
        ScheduleJobEntity job = new ScheduleJobEntity();
        job.setJobBean(TASK_BEAN + "_" + strategy.getStrategyId() + "_" + node.name().toLowerCase());
        job.setJobName("升降策略-" + strategy.getStrategyName() + "-" + node.name());
        job.setJobParams("{\"strategyId\":" + strategy.getStrategyId()
                + ",\"action\":" + action + ",\"nodeType\":\"" + node.name() + "\"}");
        job.setJobCron(cron);
        job.setJobState(JOB_STATE_NORMAL);
        job.setJobStatus(JOB_STATUS_VALID);
        job.setJobComment("策略自动生成");
        job.setJobCreatetime(now);
        job.setJobUpdatetime(now);
        scheduleJobService.save(job);
        ScheduleJobEntity full = scheduleJobService.getById(job.getJobId());
        ScheduleUtils.createScheduleJob(scheduler, full);
        applyFixedTimeZone(full.getJobId());
    }

    /** 将 biz 策略 trigger 固定为 Asia/Shanghai（保留 DoNothing misfire） */
    private void applyFixedTimeZone(Long jobId) {
        try {
            CronTrigger trigger = ScheduleUtils.getCronTrigger(scheduler, jobId);
            if (trigger == null) {
                return;
            }
            TriggerKey tk = ScheduleUtils.getTriggerKey(jobId);
            CronScheduleBuilder builder = CronScheduleBuilder
                    .cronSchedule(trigger.getCronExpression())
                    .inTimeZone(BIZ_TIMEZONE)
                    .withMisfireHandlingInstructionDoNothing();
            CronTrigger rebuilt = trigger.getTriggerBuilder()
                    .withIdentity(tk)
                    .withSchedule(builder)
                    .build();
            scheduler.rescheduleJob(tk, rebuilt);
        } catch (SchedulerException e) {
            log.warn("固定策略任务时区失败 jobId={}", jobId, e);
        }
    }

    public void removeJobs(Long strategyId) {
        for (ScheduleJobEntity job : findJobs(strategyId)) {
            scheduleJobService.deleteJob(job.getJobId());
        }
    }

    public void pauseJobs(Long strategyId) {
        for (ScheduleJobEntity job : findJobs(strategyId)) {
            scheduleJobService.pause(job.getJobId());
        }
    }

    public void resumeJobs(Long strategyId) {
        for (ScheduleJobEntity job : findJobs(strategyId)) {
            scheduleJobService.resume(job.getJobId());
        }
    }

    private List<ScheduleJobEntity> findJobs(Long strategyId) {
        return scheduleJobService.list(new LambdaQueryWrapper<ScheduleJobEntity>()
                .likeRight(ScheduleJobEntity::getJobBean, TASK_BEAN + "_" + strategyId + "_")
                .eq(ScheduleJobEntity::getJobStatus, JOB_STATUS_VALID));
    }

    private Integer reverse(Integer action) {
        return ACTION_LIFT.equals(action) ? ACTION_LOWER : ACTION_LIFT;
    }
}
