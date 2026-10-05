package com.inteink.modules.biz.service.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inteink.modules.biz.annotation.*;
import com.inteink.modules.biz.assembler.StrategyAssembler;
import com.inteink.modules.biz.constant.LiftingStrategyConstant;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import com.inteink.modules.biz.service.strategy.AbstractLiftingStrategyService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyRemoveService;
import com.inteink.modules.biz.service.validator.LiftingStrategyValidator;
import com.inteink.modules.job.entity.ScheduleJobEntity;
import com.inteink.modules.job.service.ScheduleJobService;
import com.inteink.modules.job.utils.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * 升降策略删除服务实现类
 * 修复版：保留现有AOP注解 + 还原完整删除逻辑（定时任务/明细/杆绑定清理）
 */
@Slf4j
@Service
@RequiredArgsConstructor // 保留依赖注入方式
// 保留继承父类，兼容现有AOP/基础方法
public class LiftingStrategyRemoveServiceImpl extends AbstractLiftingStrategyService implements LiftingStrategyRemoveService {

    // 还原之前的核心依赖注入
    private final StrategyAssembler strategyAssembler;
    private final BizLiftingStrategyDetailService detailService;
    private final BizLiftingStrategyRodService rodService;
    private final LiftingStrategyValidator liftingStrategyValidator;
    private final JdbcTemplate jdbcTemplate;
    private final Scheduler scheduler;
    private final ScheduleJobService scheduleJobService;

    @Override
    @Transactional(rollbackFor = Exception.class) // 还原事务注解，确保删除原子性
    // 保留所有现有AOP注解，一字不改
    @StrategyOperLog(logType = BizLiftingStrategyEnum.LOG_TYPE_REMOVE)
    @StrategyLifeCycleCheck
    @StrategyExceptionHandler
    @StrategyLogRecord
    @StrategyTimeCost
    @StrategyCacheEvict
    public StrategyResponseVO removeStrategyWithDetail(Long strategyId) {
        // ========== 1. 保留现有：查询策略 + 补充精准校验 ==========
        BizLiftingStrategy strategy = getStrategyById(strategyId);
        // 还原：精准校验策略存在且未删除
        liftingStrategyValidator.validateStrategyExistAndNotDeleted(strategy, strategyId);

        // ========== 2. 还原：删除关联定时任务（核心） ==========
        this.deleteStrategyScheduleJob(strategyId);

        // ========== 3. 保留现有：逻辑删除策略 + 补充字段 ==========
        strategy.setStrategyStatus(LiftingStrategyConstant.STRATEGY_STATUS_DELETED);
        fillBaseUpdateFields(strategy); // 保留父类方法
        // 补充：更新时间戳（双重保障）
        strategy.setStrategyUpdatetime(Instant.now().getEpochSecond());

        int update = strategyMapper.updateById(strategy);
        if (update <= 0) {
            throw StrategyBizException.systemError("策略删除失败");
        }

        // ========== 4. 还原：删除策略明细 ==========
        detailService.remove(Wrappers.<BizLiftingStrategyDetail>lambdaQuery()
                .eq(BizLiftingStrategyDetail::getStrategyId, strategyId));

        // ========== 5. 还原：删除杆绑定关系 ==========
        rodService.removeByStrategyId(strategyId);

        // ========== 6. 还原：组装返回VO（补充状态描述） ==========
        // 保留父类基础VO构建 + 补充删除后的状态信息
        StrategyResponseVO responseVO = buildSuccessVO(strategy);
        responseVO = strategyAssembler.assembleSimpleResponseVO(strategy, "删除策略成功（已解绑关联升降杆+删除定时任务）");
        // 补充删除状态值+描述
        responseVO.setStrategyStatus(LiftingStrategyConstant.STRATEGY_STATUS_DELETED);
        responseVO.setStrategyStatusDesc(BizLiftingStrategyEnum.getStrategyStatusDesc(LiftingStrategyConstant.STRATEGY_STATUS_DELETED));
        // 补充审核状态描述（兼容前端展示）
        responseVO.setStrategyCheckState(strategy.getStrategyCheckState());
        responseVO.setStrategyCheckStateDesc(BizLiftingStrategyEnum.getCheckStateDesc(strategy.getStrategyCheckState()));

        return responseVO;
    }

    // ========== 还原：私有方法 - 删除定时任务（逻辑完全不变） ==========
    private void deleteStrategyScheduleJob(Long strategyId) {
        try {
            List<Long> jobIds = jdbcTemplate.queryForList(
                    "SELECT job_id FROM schedule_job WHERE strategy_id = ?",
                    Long.class,
                    strategyId
            );

            for (Long jobId : jobIds) {
                ScheduleJobEntity job = scheduleJobService.getById(jobId);
                if (job != null) {
                    ScheduleUtils.deleteScheduleJob(scheduler, jobId);
                    scheduleJobService.removeById(jobId);
                }
            }
            log.info("策略{}关联的定时任务已删除", strategyId);
        } catch (Exception e) {
            throw StrategyBizException.systemError("删除定时任务失败：" + e.getMessage());
        }
    }
}