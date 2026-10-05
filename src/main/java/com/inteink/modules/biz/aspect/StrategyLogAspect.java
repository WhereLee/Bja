package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.annotation.StrategyOperLog;
import com.inteink.modules.biz.assembler.StrategyLogAssembler;
import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import com.inteink.modules.biz.model.vo.StrategyExecuteResultVO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.impl.BizLiftingStrategyLogServiceImpl;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class StrategyLogAspect {

    private final BizLiftingStrategyLogServiceImpl strategyLogService;
    private final LiftingRodService liftingRodService;
    private final BizLiftingStrategyRodService strategyRodService;
    private static final Long OPERATOR_DEFAULT = 0L;

    @Pointcut("@annotation(com.inteink.modules.biz.annotation.StrategyOperLog)")
    public void strategyLogPointCut() {}

    @Around("strategyLogPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        StrategyOperLog operLog = method.getAnnotation(StrategyOperLog.class);
        BizLiftingStrategyEnum logTypeEnum = operLog.logType();
        int logType = logTypeEnum.getLogTypeCode();

        Object result = null;
        try {
            result = point.proceed();
            return result;
        } finally {
            try {
                buildAndSaveLog(point, logType, result);
            } catch (Exception e) {
                log.warn("策略日志保存失败", e);
            }
        }
    }

    private void buildAndSaveLog(ProceedingJoinPoint point, int logType, Object result) {
        if (logType == BizLiftingStrategyEnum.LOG_TYPE_PAUSE.getLogTypeCode()) {buildPauseStrategyLog(point, result); return;}
        if (logType == BizLiftingStrategyEnum.LOG_TYPE_RESUME.getLogTypeCode()) {buildResumeStrategyLog(point, result); return;}
        if (logType == BizLiftingStrategyEnum.LOG_TYPE_UPDATE.getLogTypeCode()) {buildUpdateStrategyLog(point, result); return;}
        if (logType == BizLiftingStrategyEnum.LOG_TYPE_AUDIT.getLogTypeCode()) {buildAuditStrategyLog(point, result); return;}
        if (logType == BizLiftingStrategyEnum.LOG_TYPE_ADD.getLogTypeCode()) {buildAddStrategyLog(point, result); return;}
        if (logType == BizLiftingStrategyEnum.LOG_TYPE_EXECUTE.getLogTypeCode()) {buildExecuteStrategyLog(point, result); return;}
        if (logType == BizLiftingStrategyEnum.LOG_TYPE_REMOVE.getLogTypeCode()) {buildRemoveStrategyLog(point, result);}
    }

    private void buildAddStrategyLog(ProceedingJoinPoint point, Object result) {
        StrategySaveDTO saveDTO = null;
        for (Object arg : point.getArgs()) {
            if (arg instanceof StrategySaveDTO) {saveDTO = (StrategySaveDTO) arg; break;}
        }
        if (saveDTO == null) return;
        StrategyResponseVO respVO = result instanceof StrategyResponseVO ? (StrategyResponseVO) result : null;
        if (respVO == null) return;

        List<BizLiftingStrategyDetail> detailList = respVO.getDetailList();
        if (detailList.isEmpty()) return;
        BizLiftingStrategy strategy = new BizLiftingStrategy();
        strategy.setStrategyId(respVO.getStrategyId());
        strategy.setStrategyName(respVO.getStrategyName());
        strategy.setStrategyAction(respVO.getStrategyAction());
        strategy.setStrategyRemark(respVO.getStrategyRemark());

        List<Long> rodIds = new ArrayList<>();
        if (respVO.getRodList() != null && !respVO.getRodList().isEmpty()) {
            rodIds = respVO.getRodList().stream().map(LiftingRodVO::getRodId).collect(Collectors.toList());
        }
        String coreLog = StrategyLogAssembler.buildStrategyCoreLog(strategy);
        String rodInfo = getRodInfo(rodIds);
        String timePeriod = getTimePeriod(detailList);

        String actionDesc = BizLiftingStrategyEnum.getActionDesc(strategy.getStrategyAction());
        actionDesc = "未知".equals(actionDesc) ? "未配置" : actionDesc;

        String logRemark = String.format(
                "新增策略：%s,strategyAction=%s,strategyRemark=%s，绑定杆ID=%s，执行时段=%s，操作人=%s，送往审核（状态：待审核/有效）。",
                coreLog, actionDesc, strategy.getStrategyRemark(), rodInfo, timePeriod, saveDTO.getStrategyCreator()
        );
        strategyLogService.saveStrategyLog(strategy.getStrategyId(), BizLiftingStrategyEnum.LOG_TYPE_ADD.getLogTypeCode(), logRemark, saveDTO.getStrategyCreator());
    }

    private void buildUpdateStrategyLog(ProceedingJoinPoint point, Object result) {
        StrategyUpdateDTO updateDTO = null;
        for (Object arg : point.getArgs()) {
            if (arg instanceof StrategyUpdateDTO) {updateDTO = (StrategyUpdateDTO) arg; break;}
        }
        if (updateDTO == null || updateDTO.getStrategyId() == null || updateDTO.getStrategyUpdater() == null) {
            log.warn("修改日志组装失败：入参/策略ID/操作人 为空"); return;
        }
        StrategyResponseVO respVO = result instanceof StrategyResponseVO ? (StrategyResponseVO) result : null;
        if (respVO == null || respVO.getStrategyId() == null) {log.warn("修改日志组装失败：返回VO/策略ID 为空"); return;}

        List<Long> rodIds = new ArrayList<>();
        if (updateDTO.getRodIds() != null && !updateDTO.getRodIds().trim().isEmpty()) {
            String[] rodIdArr = updateDTO.getRodIds().split(",");
            for (String idStr : rodIdArr) {if (!idStr.trim().isEmpty()) rodIds.add(Long.parseLong(idStr.trim()));}
        }
        List<BizLiftingStrategyDetail> detailList = respVO.getDetailList() == null ? new ArrayList<>() : respVO.getDetailList();
        String rodInfo = getRodInfo(rodIds);
        String timePeriod = getTimePeriod(detailList);

        BizLiftingStrategy updateStrategy = new BizLiftingStrategy();
        updateStrategy.setStrategyId(respVO.getStrategyId());
        updateStrategy.setStrategyName(respVO.getStrategyName());
        updateStrategy.setStrategyAction(updateDTO.getStrategyAction());
        updateStrategy.setStrategyType(updateDTO.getStrategyType());
        updateStrategy.setStrategyDates(updateDTO.getStrategyDates());
        updateStrategy.setStrategyRemark(updateDTO.getStrategyRemark());
        updateStrategy.setStrategyUpdatetime(System.currentTimeMillis() / 1000);

        String logRemark = "";
        try {
            logRemark = StrategyLogAssembler.buildUpdateLogExtRemark(updateDTO, updateStrategy, rodInfo, timePeriod);
            if (logRemark.trim().isEmpty()) {
                logRemark = String.format("策略修改，ID：%s，操作人：%s，绑定杆：%s，执行时段：%s",
                        updateDTO.getStrategyId(), updateDTO.getStrategyUpdater(), rodInfo, timePeriod);
            }
        } catch (Exception e) {
            log.warn("日志备注组装异常，启用兜底文案", e);
            logRemark = String.format("策略修改【兜底】，策略ID：%s，操作人ID：%s", updateDTO.getStrategyId(), updateDTO.getStrategyUpdater());
        }
        strategyLogService.saveStrategyLog(updateDTO.getStrategyId(),BizLiftingStrategyEnum.LOG_TYPE_UPDATE.getLogTypeCode(),logRemark,updateDTO.getStrategyUpdater());
    }

    private void buildAuditStrategyLog(ProceedingJoinPoint point, Object result) {
        StrategyAuditDTO auditDTO = null;
        for (Object arg : point.getArgs()) {if (arg instanceof StrategyAuditDTO) {auditDTO = (StrategyAuditDTO) arg; break;}}
        if (auditDTO == null) return;
        StrategyResponseVO respVO = result instanceof StrategyResponseVO ? (StrategyResponseVO) result : null;
        if (respVO == null || respVO.getStrategyId() == null) return;

        String rejectReason = auditDTO.getReason() == null || auditDTO.getReason().trim().isEmpty() ? "未填写驳回原因" : auditDTO.getReason();
        String checkStateDesc = BizLiftingStrategyEnum.getCheckStateDesc(auditDTO.getCheckState());
        String logRemark = String.format(
                "策略审核操作：strategyId=%s，审核状态=%s(%s)，操作人=%s，驳回原因=%s",
                respVO.getStrategyId(), auditDTO.getCheckState(), checkStateDesc, auditDTO.getOperatorId(), rejectReason
        );
        strategyLogService.saveStrategyLog(respVO.getStrategyId(), BizLiftingStrategyEnum.LOG_TYPE_AUDIT.getLogTypeCode(), logRemark, auditDTO.getOperatorId());
    }

    private void buildPauseStrategyLog(ProceedingJoinPoint point, Object result) {
        Long strategyId = null; Long operator = null;
        Object[] args = point.getArgs();
        if (args != null && args.length >= 2) {strategyId = (Long) args[0]; operator = (Long) args[1];}
        if (strategyId == null || operator == null) {log.warn("暂停日志组装失败：策略ID/操作人 为空"); return;}
        StrategyResponseVO respVO = result instanceof StrategyResponseVO ? (StrategyResponseVO) result : null;
        if (respVO == null) return;

        BizLiftingStrategy strategy = new BizLiftingStrategy();
        strategy.setStrategyId(respVO.getStrategyId()); strategy.setStrategyName(respVO.getStrategyName());
        strategy.setStrategyAction(respVO.getStrategyAction()); strategy.setStrategyRemark(respVO.getStrategyRemark());
        strategy.setStrategyStatus(BizLiftingStrategyEnum.STRATEGY_STATUS_PAUSED.getStrategyStatusCode());

        String coreLog = StrategyLogAssembler.buildStrategyCoreLog(strategy);
        String actionDesc = BizLiftingStrategyEnum.getActionDesc(strategy.getStrategyAction());
        String statusDesc = BizLiftingStrategyEnum.STRATEGY_STATUS_PAUSED.getDesc();

        String logRemark = String.format(
                "暂停策略：%s,strategyAction=%s,strategyRemark=%s，操作人=%s，策略状态置为%s（%s），已同步暂停关联定时任务。",
                coreLog, actionDesc, strategy.getStrategyRemark(), operator, BizLiftingStrategyEnum.STRATEGY_STATUS_PAUSED.getCode(), statusDesc
        );
        strategyLogService.saveStrategyLog(strategyId,BizLiftingStrategyEnum.LOG_TYPE_PAUSE.getLogTypeCode(),logRemark,operator);
    }

    private void buildResumeStrategyLog(ProceedingJoinPoint point, Object result) {
        Long strategyId = null; Long operator = null;
        Object[] args = point.getArgs();
        if (args != null && args.length >= 2) {strategyId = (Long) args[0]; operator = (Long) args[1];}
        if (strategyId == null || operator == null) {log.warn("恢复日志组装失败：策略ID/操作人 为空"); return;}
        StrategyResponseVO respVO = result instanceof StrategyResponseVO ? (StrategyResponseVO) result : null;
        if (respVO == null) return;

        BizLiftingStrategy strategy = new BizLiftingStrategy();
        strategy.setStrategyId(respVO.getStrategyId()); strategy.setStrategyName(respVO.getStrategyName());
        strategy.setStrategyAction(respVO.getStrategyAction()); strategy.setStrategyRemark(respVO.getStrategyRemark());
        strategy.setStrategyStatus(BizLiftingStrategyEnum.STRATEGY_STATUS_VALID.getStrategyStatusCode());

        String coreLog = StrategyLogAssembler.buildStrategyCoreLog(strategy);
        String actionDesc = BizLiftingStrategyEnum.getActionDesc(strategy.getStrategyAction());
        String statusDesc = BizLiftingStrategyEnum.STRATEGY_STATUS_VALID.getDesc();

        String logRemark = String.format(
                "恢复策略：%s,strategyAction=%s,strategyRemark=%s，操作人=%s，策略状态置为%s（%s），已同步恢复关联定时任务。",
                coreLog, actionDesc, strategy.getStrategyRemark(), operator, BizLiftingStrategyEnum.STRATEGY_STATUS_VALID.getCode(), statusDesc
        );
        strategyLogService.saveStrategyLog(strategyId,BizLiftingStrategyEnum.LOG_TYPE_RESUME.getLogTypeCode(),logRemark,operator);
    }

    private void buildExecuteStrategyLog(ProceedingJoinPoint point, Object result) {
        Long strategyId = null; Integer action = null;
        Object[] args = point.getArgs();
        if (args != null && args.length >= 2) {strategyId = (Long) args[0]; action = (Integer) args[1];}
        if (strategyId == null) {log.warn("执行日志组装失败：策略ID 为空"); return;}
        StrategyExecuteResultVO respVO = result instanceof StrategyExecuteResultVO ? (StrategyExecuteResultVO) result : null;
        if (respVO == null) return;

        List<Long> rodIds = strategyRodService.getRodIdsByStrategyId(strategyId);
        String rodInfo = getRodInfo(rodIds);
        String actionDesc = respVO.getActionDesc() == null ? "未知动作" : respVO.getActionDesc();

        String logRemark = String.format(
                "执行策略：strategyId=%s，策略名称=%s，执行动作=%s，绑定杆=%s，执行结果：总数=%s根，成功=%s根，失败=%s根，跳过=%s根，执行详情：%s，操作人=%s。",
                strategyId, respVO.getStrategyName(), actionDesc, rodInfo,
                respVO.getTotalRodCount(), respVO.getSuccessCount(), respVO.getFailCount(), respVO.getSkipCount(),
                respVO.getExecuteMsg(), OPERATOR_DEFAULT
        );
        strategyLogService.saveStrategyLog(strategyId,BizLiftingStrategyEnum.LOG_TYPE_EXECUTE.getLogTypeCode(),logRemark,OPERATOR_DEFAULT);
    }

    private void buildRemoveStrategyLog(ProceedingJoinPoint point, Object result) {
        Long strategyId = null;
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {strategyId = (Long) args[0];}
        if (strategyId == null) {log.warn("删除日志组装失败：策略ID为空"); return;}

        StrategyResponseVO respVO = result instanceof StrategyResponseVO ? (StrategyResponseVO) result : null;
        if (respVO == null) return;

        List<Long> rodIds = strategyRodService.getRodIdsByStrategyId(strategyId);
        String rodInfo = getRodInfo(rodIds);
        String statusDesc = BizLiftingStrategyEnum.STRATEGY_STATUS_DELETED.getDesc();

        String logRemark = String.format(
                "删除策略：strategyId=%s，策略名称=%s，操作人=%s，策略状态置为%s（%s），已删除关联定时任务、策略明细，已解绑绑定杆【%s】，删除结果：删除策略成功（已解绑关联升降杆+删除定时任务）。",
                strategyId, respVO.getStrategyName(), OPERATOR_DEFAULT, BizLiftingStrategyEnum.STRATEGY_STATUS_DELETED.getCode(), statusDesc, rodInfo
        );
        strategyLogService.saveStrategyLog(strategyId,BizLiftingStrategyEnum.LOG_TYPE_REMOVE.getLogTypeCode(),logRemark,OPERATOR_DEFAULT);
    }

    private String getRodInfo(List<Long> rodIds) {
        if (rodIds.isEmpty()) return "无";
        List<LiftingRodVO> rodList = liftingRodService.getRodDetailListByIds(rodIds);
        if (rodList.isEmpty()) return rodIds.toString();
        StringJoiner joiner = new StringJoiner("、");
        rodList.forEach(rod -> joiner.add(rod.getRodId() + "(" + rod.getRodName() + ")"));
        return joiner.toString();
    }

    private String getTimePeriod(List<BizLiftingStrategyDetail> detailList) {
        if (detailList.isEmpty()) return "无";
        BizLiftingStrategyDetail firstDetail = detailList.get(0);
        if (firstDetail.getDetailEnd() == null || firstDetail.getDetailEnd().isEmpty()) {
            return firstDetail.getDetailBegin();
        }
        return firstDetail.getDetailBegin() + "-" + firstDetail.getDetailEnd();
    }
}