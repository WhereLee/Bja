package com.inteink.modules.biz.factory;

import com.inteink.common.exception.RRException;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
import com.inteink.modules.biz.utils.StrategyCronUtil;
import com.inteink.modules.job.entity.ScheduleJobEntity;
import com.inteink.modules.job.service.ScheduleJobService;
import com.inteink.modules.job.utils.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.quartz.Scheduler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * 升降策略定时任务工厂类
 * 核心职责：封装定时任务的构建、保存、绑定策略ID、创建Quartz任务全流程
 * 解耦：让业务类无需关心定时任务的具体创建逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StrategyScheduleFactory {

    private final BizLiftingStrategyDetailService detailService;
    private final ScheduleJobService scheduleJobService;
    private final Scheduler scheduler;
    private final JdbcTemplate jdbcTemplate;

    // 策略动作：1-升杆 2-降杆
    private static final Integer ACTION_LIFT = 1;
    private static final Integer ACTION_LOWER = 2;
    // 节点类型：BEGIN-开始节点 END-结束节点（传给Task的参数）
    private static final String NODE_TYPE_BEGIN = "BEGIN";
    private static final String NODE_TYPE_END = "END";
    // 错误码：500-业务错误
    private static final int BIZ_ERROR_CODE = 500;

    /**
     * 核心入口：为审核通过的策略生成定时任务（仅存在begin时只生成begin任务，有end时生成成对任务）
     * @param strategy 审核通过的策略实体
     */
    public void createStrategyScheduleJob(BizLiftingStrategy strategy) {
        Long strategyId = strategy.getStrategyId();
        try {
            // 1. 查询策略明细（校验非空）
            List<BizLiftingStrategyDetail> detailList = detailService.getByStrategyId(strategyId);
            if (detailList == null || detailList.isEmpty()) {
                throw new RRException("策略无执行明细，无法生成定时任务：" + strategyId, 404);
            }
            BizLiftingStrategyDetail detail = detailList.get(0);

            // 2. 生成开始任务（目标动作：升杆/降杆）- 必选，绑定BEGIN节点类型
            String beginCron = StrategyCronUtil.buildCron(strategy, detail);
            if (StringUtils.isNotBlank(beginCron)) {
                ScheduleJobEntity beginJob = buildScheduleJob(strategy, detail, "begin", NODE_TYPE_BEGIN, strategy.getStrategyAction(), beginCron);
                saveAndBindScheduleJob(beginJob, strategyId);
                log.info("策略begin定时任务生成成功，策略ID：{}", strategyId);
            } else {
                throw new RRException("策略begin Cron表达式为空，无法生成开始定时任务：" + strategyId, 400);
            }

            // 3. 生成结束任务（反向动作：降杆/升杆）- 可选，绑定END节点类型
            Integer reverseAction = Objects.equals(strategy.getStrategyAction(), ACTION_LIFT) ? ACTION_LOWER : ACTION_LIFT;
            String endCron = StrategyCronUtil.buildCron(strategy, detail, true);
            if (StringUtils.isNotBlank(endCron)) {
                ScheduleJobEntity endJob = buildScheduleJob(strategy, detail, "end", NODE_TYPE_END, reverseAction, endCron);
                saveAndBindScheduleJob(endJob, strategyId);
                log.info("策略end定时任务生成成功，策略ID：{}", strategyId);
            } else {
                log.warn("策略end Cron表达式为空，跳过结束定时任务生成，策略ID：{}", strategyId);
            }

            log.info("策略定时任务生成流程完成，策略ID：{}", strategyId);
        } catch (RRException e) {
            throw e; // 业务异常直接抛出
        } catch (Exception e) {
            String errorMsg = "策略" + strategyId + "生成定时任务失败";
            log.error(errorMsg, e);
            throw new RRException(errorMsg + "：" + e.getMessage(), BIZ_ERROR_CODE);
        }
    }

    /**
     * 构建定时任务实体（新增nodeType参数，区分BEGIN/END节点）
     */
    private ScheduleJobEntity buildScheduleJob(BizLiftingStrategy strategy,
                                               BizLiftingStrategyDetail detail,
                                               String taskFlag,
                                               String nodeType, // 新增：节点类型（BEGIN/END）
                                               Integer action,
                                               String cron) {
        Long strategyId = strategy.getStrategyId();
        String strategyName = strategy.getStrategyName();

        // 1. 构建任务唯一标识（适配框架的JobKey规则）
        String jobBean = String.format("liftingStrategyTask_%s_%s", strategyId, taskFlag);

        // 2. 构建任务名称（含策略信息，便于排查）
        String actionName = Objects.equals(action, ACTION_LIFT) ? "升杆" : "降杆";
        String jobName = String.format("升降策略-%s-%s-%s", strategyName, taskFlag, actionName);

        // 3. 构建任务参数（新增nodeType字段，传给Task类识别节点类型）
        String jobParams = String.format("{\"strategyId\":\"%s\", \"action\":\"%s\", \"nodeType\":\"%s\"}",
                strategyId, action, nodeType);

        // 4. 构建任务实体（严格适配框架的ScheduleJobEntity，不修改原有构造器）
        ScheduleJobEntity job = new ScheduleJobEntity();
        job.setJobBean(jobBean);
        job.setJobName(jobName);
        job.setJobParams(jobParams);
        job.setJobCron(cron);
        job.setJobState(0); // 0-正常（框架常量：Constant.ScheduleStatus.NORMAL）
        job.setJobComment(StringUtils.defaultIfBlank(strategy.getStrategyRemark(), "策略自动生成"));
        job.setJobCreatetime(Instant.now().getEpochSecond());
        job.setJobUpdatetime(Instant.now().getEpochSecond());
        job.setJobStatus(0L); // 0-有效

        return job;
    }

    /**
     * 保存定时任务+绑定策略ID+创建Quartz任务（原有逻辑无修改）
     */
    private void saveAndBindScheduleJob(ScheduleJobEntity job, Long strategyId) {
        // 1. 保存任务（适配框架的ScheduleJobService接口）
        boolean saveSuccess = scheduleJobService.save(job);
        if (!saveSuccess) {
            throw new RRException("定时任务保存失败：" + job.getJobName(), BIZ_ERROR_CODE);
        }

        // 2. 校验任务ID（框架生成的主键）
        Long jobId = job.getJobId();
        if (jobId == null) {
            throw new RRException("定时任务未生成jobId：" + job.getJobName(), BIZ_ERROR_CODE);
        }

        // 3. 绑定策略ID到定时任务（复用原SQL，JdbcTemplate由框架保证可用）
        String updateSql = "UPDATE schedule_job SET strategy_id = ? WHERE job_id = ?";
        int updateCount = jdbcTemplate.update(updateSql, strategyId, jobId);
        if (updateCount == 0) {
            throw new RRException("定时任务绑定策略ID失败：" + job.getJobName(), BIZ_ERROR_CODE);
        }

        // 4. 创建Quartz任务（调用框架的ScheduleUtils，不可修改）
        ScheduleUtils.createScheduleJob(scheduler, job);
        log.info("定时任务创建成功，任务名称：{}，策略ID：{}", job.getJobName(), strategyId);
    }

}