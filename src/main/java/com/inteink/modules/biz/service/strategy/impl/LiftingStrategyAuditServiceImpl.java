package com.inteink.modules.biz.service.strategy.impl;

import com.inteink.common.utils.Result;
import com.inteink.modules.biz.annotation.*;
import com.inteink.modules.biz.assembler.StrategyAssembler;
import com.inteink.modules.biz.assembler.StrategyLogAssembler;

import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.factory.StrategyScheduleFactory;
import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;

import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.rule.StrategyRuleEngine;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.strategy.AbstractLiftingStrategyService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyAuditService;
import com.inteink.modules.biz.service.validator.LiftingStrategyValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

/**
 * 升降策略审核服务实现类
 * 
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiftingStrategyAuditServiceImpl extends AbstractLiftingStrategyService implements LiftingStrategyAuditService {

    // 注入历史代码中的核心依赖
    private final StrategyRuleEngine strategyRuleEngine;
    private final StrategyAssembler strategyAssembler;
    private final StrategyScheduleFactory strategyScheduleFactory;
    private final LiftingStrategyValidator liftingStrategyValidator;
    private final com.inteink.modules.biz.service.impl.BizLiftingStrategyLogServiceImpl strategyLogService;
    private final BizLiftingStrategyMapper strategyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // 融合历史：添加事务注解
    @StrategyOperLog(logType = BizLiftingStrategyEnum.LOG_TYPE_AUDIT) // 保留原有AOP
    @StrategyLifeCycleCheck // 保留原有AOP
    @StrategyExceptionHandler // 保留原有AOP
    @StrategyLogRecord // 保留原有AOP
    @StrategyTimeCost // 保留原有AOP
    @StrategyCacheEvict // 保留原有AOP
    public Result<StrategyResponseVO> auditStrategy(StrategyAuditDTO auditDTO) {
        // ========== 1. 融合历史：入参+策略前置校验 ==========
        // 1.1 校验入参合法性（审核状态/驳回原因/操作人等）
        strategyRuleEngine.validateAuditDTO(auditDTO);

        // 1.2 查询策略（复用父类逻辑+历史校验）
        BizLiftingStrategy strategy = getStrategyById(auditDTO.getStrategyId());
        // 1.3 校验策略存在且未删除
        liftingStrategyValidator.validateStrategyExistAndNotDeleted(strategy, auditDTO.getStrategyId());
        // 1.4 校验审核前置规则（待审核状态）
        liftingStrategyValidator.validateStrategyAuditPreRule(strategy);

        log.info("开始审核策略，策略ID：{}，操作人：{}，目标审核状态：{}",
                auditDTO.getStrategyId(), auditDTO.getOperatorId(), auditDTO.getCheckState());

        // ========== 2. 融合历史：记录原始状态+更新审核字段 ==========
        // 2.1 记录原始审核状态（用于异常回滚）
        Integer originalCheckState = strategy.getStrategyCheckState();

        // 2.2 更新审核核心字段（保留原有字段更新逻辑 + 融合历史时间戳）
        strategy.setStrategyCheckState(auditDTO.getCheckState());
        strategy.setStrategyCheckUser(auditDTO.getOperatorId());
        strategy.setStrategyCheckTime(Instant.now().getEpochSecond()); // 融合历史：时间戳优化
        strategy.setStrategyUpdatetime(Instant.now().getEpochSecond()); // 融合历史：更新时间戳
        fillBaseUpdateFields(strategy); // 保留原有：填充基础更新字段

        // 2.3 执行数据库更新（融合历史：判断更新结果）
        boolean updateSuccess = strategyMapper.updateById(strategy) > 0;
        if (!updateSuccess) {
            throw StrategyBizException.systemError("策略审核更新失败，策略ID：" + auditDTO.getStrategyId());
        }
        log.info("策略审核更新成功，策略ID：{}，更新行数：{}", auditDTO.getStrategyId(), updateSuccess ? 1 : 0);

        // ========== 3. 融合历史：审核结果差异化处理（通过/驳回） ==========
        String msg;
        String scheduleJobId = null;
        try {
            if (BizLiftingStrategyEnum.CHECK_STATE_PASS.getCode().equals(auditDTO.getCheckState())) {
                // 3.1 审核通过：创建定时任务
                strategyScheduleFactory.createStrategyScheduleJob(strategy);
                msg = "审核通过，已生成定时执行任务";
            } else {
                // 3.2 审核驳回：拼接驳回原因
                msg = "审核驳回，原因：" + (auditDTO.getReason() == null ? "" : auditDTO.getReason().trim());
            }
        } catch (Exception e) {
            log.error("【策略审核】定时任务处理失败，策略ID：{}，审核状态：{}", auditDTO.getStrategyId(), auditDTO.getCheckState(), e);
            // 3.3 异常回滚：恢复原始审核状态
            strategy.setStrategyCheckState(originalCheckState);
            strategyMapper.updateById(strategy);
            throw StrategyBizException.systemError("审核操作失败：" + e.getMessage());
        }

        // ========== 4. 融合历史：记录审核日志（容错设计） ==========
        try {
            String logRemark = StrategyLogAssembler.buildAuditLogRemark(strategy, auditDTO, scheduleJobId, auditDTO.getReason());
            strategyLogService.saveStrategyLog(
                    strategy.getStrategyId(),
                    BizLiftingStrategyEnum.LOG_TYPE_AUDIT.getLogTypeCode(), // 替换魔法值为枚举
                    logRemark,
                    auditDTO.getOperatorId()
            );
        } catch (Exception e) {
            log.warn("【策略审核】日志记录失败，策略ID：{}", auditDTO.getStrategyId(), e);
            // 日志失败不阻断主流程
        }

        // ========== 5. 融合历史：组装返回VO（标准化+兜底） ==========
        StrategyResponseVO vo = buildSuccessVO(strategy); // 保留原有：构建基础VO
        // 5.1 补充返回信息+状态描述修正
        vo = strategyAssembler.assembleSimpleResponseVO(strategy, msg);
        if (vo.getStrategyStatus() != null && vo.getStrategyStatus() == 0) {
            vo.setStrategyStatusDesc("有效"); // 修正状态描述
        }
        // 5.2 空列表兜底（避免前端NPE）
        if (vo.getRodList() == null) {
            vo.setRodList(new ArrayList<>());
        }
        if (vo.getDetailList() == null) {
            vo.setDetailList(new ArrayList<>());
        }

        // ========== 6. 保留原有：包装Result返回 ==========
        return Result.ok(vo);
    }
}