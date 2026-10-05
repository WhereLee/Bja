package com.inteink.modules.biz.service.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inteink.modules.biz.annotation.*;
import com.inteink.modules.biz.assembler.StrategyLogAssembler;
import com.inteink.modules.biz.constant.LiftingStrategyConstant;

import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.strategy.AbstractLiftingStrategyService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyPauseResumeService;
import com.inteink.modules.job.entity.ScheduleJobEntity;
import com.inteink.modules.job.service.ScheduleJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * 策略暂停/恢复服务实现类
 * 修复版：保留现有AOP注解 + 还原定时任务同步/精准校验/结构化日志逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor // 替换原有注入方式，兼容依赖
// 保留继承父类，复用基础方法+AOP
public class LiftingStrategyPauseResumeServiceImpl extends AbstractLiftingStrategyService implements LiftingStrategyPauseResumeService {

    // 复用原有常量，对齐系统常量定义
    private static final Long STRATEGY_STATUS_VALID = LiftingStrategyConstant.STRATEGY_STATUS_VALID;    // 策略有效
    private static final Long STRATEGY_STATUS_PAUSED = LiftingStrategyConstant.STRATEGY_STATUS_PAUSED;   // 策略暂停

    // 注入核心依赖
    private final com.inteink.modules.biz.service.impl.BizLiftingStrategyLogServiceImpl strategyLogService;
    private final StrategyLogAssembler strategyLogAssembler;
    private final ScheduleJobService scheduleJobService;

    /**
     * 暂停策略（保留AOP + 还原定时任务同步/精准校验）
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 还原事务注解
    // 保留所有现有AOP注解，一字不改
    @StrategyOperLog(logType = BizLiftingStrategyEnum.LOG_TYPE_PAUSE)
    @StrategyLifeCycleCheck
    @StrategyExceptionHandler
    @StrategyLogRecord
    @StrategyTimeCost
    @StrategyCacheEvict
    public StrategyResponseVO pauseStrategy(Long strategyId, Long operator) {
        // ========== 1. 保留现有：查询策略 + 还原精准校验 ==========
        BizLiftingStrategy strategy = getStrategyById(strategyId); // 复用父类方法
        // 精准校验：策略存在
        if (ObjectUtils.isEmpty(strategy)) {
            throw StrategyBizException.systemError("策略不存在，无法暂停！strategyId=" + strategyId);
        }
        // 精准校验：仅有效状态可暂停
        if (!Objects.equals(strategy.getStrategyStatus(), STRATEGY_STATUS_VALID)) {
            throw StrategyBizException.systemError(
                    String.format("策略当前状态为%s（%s），仅有效状态可暂停！strategyId=%s",
                            strategy.getStrategyStatus(),
                            BizLiftingStrategyEnum.getStrategyStatusDesc(strategy.getStrategyStatus()),
                            strategyId)
            );
        }

        // ========== 2. 保留现有：更新策略状态 + 补充字段 ==========
        strategy.setStrategyStatus(LiftingStrategyConstant.STRATEGY_STATUS_PAUSED); // 对齐系统常量
        fillBaseUpdateFields(strategy); // 保留父类方法
        strategy.setStrategyUpdatetime(Instant.now().getEpochSecond()); // 补充时间戳双重保障

        // 保留现有更新逻辑，优化返回值判断
        int update = strategyMapper.updateById(strategy);
        if (update <= 0) {
            throw StrategyBizException.systemError("策略暂停失败！strategyId=" + strategyId);
        }

        // ========== 3. 还原：同步暂停关联定时任务（核心） ==========
        syncScheduleJob(strategyId, true);

        // ========== 4. 还原：保存暂停操作日志 ==========
        savePauseResumeLog(strategy, BizLiftingStrategyEnum.LOG_TYPE_PAUSE.getLogTypeCode(), operator);

        // ========== 5. 保留现有：构建返回VO + 补充状态描述 ==========
        StrategyResponseVO vo = buildSuccessVO(strategy); // 复用父类VO方法
        // 补充状态描述（前端友好）
        vo.setStrategyCheckStateDesc(BizLiftingStrategyEnum.getCheckStateDesc(strategy.getStrategyCheckState()));
        vo.setStrategyStatusDesc(BizLiftingStrategyEnum.getStrategyStatusDesc(strategy.getStrategyStatus()));
        return vo;
    }

    /**
     * 恢复策略（保留AOP + 还原定时任务同步/精准校验）
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 还原事务注解
    // 保留所有现有AOP注解，一字不改
    @StrategyOperLog(logType = BizLiftingStrategyEnum.LOG_TYPE_RESUME)
    @StrategyLifeCycleCheck
    @StrategyExceptionHandler
    @StrategyLogRecord
    @StrategyTimeCost
    @StrategyCacheEvict
    public StrategyResponseVO resumeStrategy(Long strategyId, Long operator) {
        // ========== 1. 保留现有：查询策略 + 还原精准校验 ==========
        BizLiftingStrategy strategy = getStrategyById(strategyId); // 复用父类方法
        // 精准校验：策略存在
        if (ObjectUtils.isEmpty(strategy)) {
            throw StrategyBizException.systemError("策略不存在，无法恢复！strategyId=" + strategyId);
        }
        // 精准校验：仅暂停状态可恢复
        if (!Objects.equals(strategy.getStrategyStatus(), STRATEGY_STATUS_PAUSED)) {
            throw StrategyBizException.systemError(
                    String.format("策略当前状态为%s（%s），仅暂停状态可恢复！strategyId=%s",
                            strategy.getStrategyStatus(),
                            BizLiftingStrategyEnum.getStrategyStatusDesc(strategy.getStrategyStatus()),
                            strategyId)
            );
        }

        // ========== 2. 保留现有：更新策略状态 + 补充字段 ==========
        strategy.setStrategyStatus(LiftingStrategyConstant.STRATEGY_STATUS_VALID); // 对齐系统常量
        fillBaseUpdateFields(strategy); // 保留父类方法
        strategy.setStrategyUpdatetime(Instant.now().getEpochSecond()); // 补充时间戳双重保障

        // 保留现有更新逻辑，优化返回值判断
        int update = strategyMapper.updateById(strategy);
        if (update <= 0) {
            throw StrategyBizException.systemError("策略恢复失败！strategyId=" + strategyId);
        }

        // ========== 3. 还原：同步恢复关联定时任务（核心） ==========
        syncScheduleJob(strategyId, false);

        // ========== 4. 还原：保存恢复操作日志 ==========
        savePauseResumeLog(strategy, BizLiftingStrategyEnum.LOG_TYPE_RESUME.getLogTypeCode(), operator);

        // ========== 5. 保留现有：构建返回VO + 补充状态描述 ==========
        StrategyResponseVO vo = buildSuccessVO(strategy); // 复用父类VO方法
        // 补充状态描述（前端友好）
        vo.setStrategyCheckStateDesc(BizLiftingStrategyEnum.getCheckStateDesc(strategy.getStrategyCheckState()));
        vo.setStrategyStatusDesc(BizLiftingStrategyEnum.getStrategyStatusDesc(strategy.getStrategyStatus()));
        return vo;
    }

    /**
     * 还原：同步定时任务状态（兼容实体类无strategyId字段）
     */
    private void syncScheduleJob(Long strategyId, boolean isPause) {
        try {
            // 用QueryWrapper手动指定数据库字段名，避开实体类无strategyId的问题
            List<ScheduleJobEntity> jobList = scheduleJobService.list(
                    new QueryWrapper<ScheduleJobEntity>()
                            .eq("job_status", 0L)  // 仅查有效任务（数据库字段名）
                            .eq("strategy_id", strategyId) // 直接用数据库字段关联策略ID
            );

            if (CollectionUtils.isEmpty(jobList)) {
                log.warn("策略{}未关联任何有效定时任务，无需同步任务状态", strategyId);
                return;
            }

            String operateType = isPause ? "暂停" : "恢复";
            for (ScheduleJobEntity job : jobList) {
                try {
                    if (isPause) {
                        scheduleJobService.pause(job.getJobId()); // 复用已有暂停方法
                    } else {
                        scheduleJobService.resume(job.getJobId()); // 复用已有恢复方法
                    }
                    log.info("策略{}的定时任务{}成功：jobId={}", strategyId, operateType, job.getJobId());
                } catch (Exception e) {
                    log.error("策略{}的定时任务{}失败：jobId={}", strategyId, operateType, job.getJobId(), e);
                }
            }
        } catch (Exception e) {
            log.error("同步策略{}的定时任务状态失败", strategyId, e);
        }
    }

    /**
     * 还原：保存暂停/恢复操作日志（结构化+容错）
     */
    private void savePauseResumeLog(BizLiftingStrategy strategy, Integer logType, Long operator) {
        try {
            String coreLog = strategyLogAssembler.buildStrategyCoreLog(strategy);
            String operateDesc = logType.equals(BizLiftingStrategyEnum.LOG_TYPE_PAUSE.getLogTypeCode()) ? "暂停" : "恢复";
            String targetStatus = logType.equals(BizLiftingStrategyEnum.LOG_TYPE_PAUSE.getLogTypeCode()) ?
                    STRATEGY_STATUS_PAUSED + "（" + BizLiftingStrategyEnum.getStrategyStatusDesc(STRATEGY_STATUS_PAUSED) + "）" :
                    STRATEGY_STATUS_VALID + "（" + BizLiftingStrategyEnum.getStrategyStatusDesc(STRATEGY_STATUS_VALID) + "）";

            String logRemark = String.format(
                    "%s策略：%s,strategyAction=%s,strategyRemark=%s，操作人=%s，策略状态置为%s，已同步%s关联定时任务。",
                    operateDesc,
                    coreLog,
                    BizLiftingStrategyEnum.getActionDesc(strategy.getStrategyAction()),
                    strategy.getStrategyRemark(),
                    operator,
                    targetStatus,
                    operateDesc
            );

            strategyLogService.saveStrategyLog(
                    strategy.getStrategyId(),
                    logType,
                    logRemark,
                    operator
            );
        } catch (Exception e) {
            log.warn("{}策略日志保存失败，策略ID：{}", logType.equals(BizLiftingStrategyEnum.LOG_TYPE_PAUSE.getLogTypeCode()) ? "暂停" : "恢复", strategy.getStrategyId(), e);
        }
    }
}