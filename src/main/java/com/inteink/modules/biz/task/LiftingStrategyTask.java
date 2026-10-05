package com.inteink.modules.biz.task;

import com.alibaba.fastjson.JSONObject;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
import com.inteink.modules.biz.utils.RodRetryUtil;
import com.inteink.modules.biz.model.vo.StrategyExecuteResultVO;
import com.inteink.modules.job.task.ITask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 升降杆策略定时任务执行类
 * Bean名称：liftingStrategyTask（框架通过该名称匹配执行）
 * 核心逻辑：处理begin/end时间节点执行、离线杆重试、一次性策略状态更新
 */
@Slf4j
@Component("liftingStrategyTask")
@RequiredArgsConstructor
public class LiftingStrategyTask implements ITask {

    // ========== 核心常量定义 ==========
    private static final Long STATUS_VALID = 0L;               // 策略/杆有效状态
    private static final Integer ACTION_LIFT = 1;              // 升杆动作
    private static final Integer ACTION_LOWER = 2;             // 降杆动作
    private static final Long OPERATOR_DEFAULT = 0L;           // 默认操作人
    private static final Long STRATEGY_STATUS_FINISHED = 5L;   // 策略已结束状态
    private static final Integer STRATEGY_TYPE_ONCE = 4;       // 一次性策略类型（指定日期）
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    // 节点类型常量（与StrategyScheduleFactory保持一致）
    private static final String NODE_TYPE_BEGIN = "BEGIN";
    private static final String NODE_TYPE_END = "END";

    // ========== 依赖注入 ==========
    private final LiftingStrategyService liftingStrategyService;
    private final BizLiftingStrategyRodService strategyRodService;
    private final BizLiftingStrategyDetailService strategyDetailService;
    private final LiftingRodService liftingRodService;

    @Override
    public void run(String params) {
        // 初始化执行结果
        StrategyExecuteResultVO result = new StrategyExecuteResultVO();

        try {
            // 解析参数（含nodeType）
            JSONObject paramJson = parseParams(params, result);
            if (paramJson == null) {
                return;
            }
            Long strategyId = paramJson.getLong("strategyId");
            Integer actionFromParam = paramJson.getInteger("action");
            String nodeType = paramJson.getString("nodeType"); // 解析节点类型
            result.setStrategyId(strategyId);

            // 查询并校验策略
            BizLiftingStrategy strategy = queryAndValidateStrategy(strategyId, result);
            if (strategy == null) {
                return;
            }
            result.setStrategyName(strategy.getStrategyName());

            // 确定执行动作
            Integer actionCode = determineActionCode(actionFromParam, strategy, result);
            if (actionCode == null) {
                return;
            }
            String actionDesc = getActionDesc(actionCode);
            result.setActionCode(actionCode);
            result.setActionDesc(actionDesc);

            // 查询策略绑定的杆（单杆场景取第一个）
            Long rodId = getBindRodId(strategyId, result);
            if (rodId == null) {
                return;
            }

            // 查询策略时间明细（begin/end）
            BizLiftingStrategyDetail detail = getStrategyDetail(strategyId);
            if (detail == null) {
                String errorMsg = "策略无执行明细，执行终止！strategyId=" + strategyId;
                log.error(errorMsg);
                result.setExecuteMsg(errorMsg);
                return;
            }

            // 根据节点类型执行对应逻辑
            if (NODE_TYPE_BEGIN.equals(nodeType)) {
                // BEGIN节点：仅执行Begin逻辑（校验begin时间非空）
                if (detail.getDetailBegin() == null || detail.getDetailBegin().isEmpty()) {
                    String errorMsg = "BEGIN节点策略无有效开始时间，执行终止！strategyId=" + strategyId;
                    log.error(errorMsg);
                    result.setExecuteMsg(errorMsg);
                    return;
                }
                handleBeginTimeExecute(strategy, detail, rodId, actionCode, result);
            } else if (NODE_TYPE_END.equals(nodeType)) {
                // END节点：仅执行End逻辑（校验end时间非空）
                if (detail.getDetailEnd() == null || detail.getDetailEnd().isEmpty()) {
                    String errorMsg = "END节点策略无有效结束时间，执行终止！strategyId=" + strategyId;
                    log.error(errorMsg);
                    result.setExecuteMsg(errorMsg);
                    return;
                }
                handleEndTimeExecute(strategy, detail, rodId, actionCode, result);
            } else {
                // 无有效节点类型（兼容旧任务）
                String errorMsg = "未识别的节点类型：" + nodeType + "，策略ID：" + strategyId;
                log.error(errorMsg);
                result.setExecuteMsg(errorMsg);
                return;
            }

            // 打印最终结果
            printFinalResult(result);

        } catch (Exception e) {
            // 全局异常处理
            String errorMsg = "升降杆定时任务执行异常：" + e.getMessage();
            log.error("【升降杆定时任务】执行异常！params={}", params, e);
            result.setExecuteMsg(errorMsg);
            printFinalResult(result);
        }
    }

    /**
     * 解析入参并校验
     */
    private JSONObject parseParams(String params, StrategyExecuteResultVO result) {
        if (params == null || params.trim().isEmpty()) {
            String errorMsg = "定时任务参数为空！";
            log.error(errorMsg);
            result.setExecuteMsg(errorMsg);
            return null;
        }

        JSONObject paramJson;
        try {
            paramJson = JSONObject.parseObject(params);
        } catch (Exception e) {
            String errorMsg = "参数格式错误，非合法JSON！params=" + params;
            log.error(errorMsg, e);
            result.setExecuteMsg(errorMsg);
            return null;
        }

        Long strategyId = paramJson.getLong("strategyId");
        if (strategyId == null) {
            String errorMsg = "参数中无strategyId，执行终止！";
            log.error(errorMsg);
            result.setExecuteMsg(errorMsg);
            return null;
        }

        String nodeType = paramJson.getString("nodeType");
        if (nodeType == null || (!NODE_TYPE_BEGIN.equals(nodeType) && !NODE_TYPE_END.equals(nodeType))) {
            String errorMsg = "参数中nodeType无效（仅支持BEGIN/END），strategyId=" + strategyId;
            log.error(errorMsg);
            result.setExecuteMsg(errorMsg);
            return null;
        }

        return paramJson;
    }

    /**
     * 查询并校验策略有效性
     * @return 有效策略对象（无效返回null）
     */
    private BizLiftingStrategy queryAndValidateStrategy(Long strategyId, StrategyExecuteResultVO result) {
        // 校验策略是否存在
        BizLiftingStrategy strategy = liftingStrategyService.getById(strategyId);
        if (strategy == null) {
            String errorMsg = "策略不存在！strategyId=" + strategyId;
            log.error(errorMsg);
            result.setExecuteMsg(errorMsg);
            return null;
        }

        // 校验策略状态是否有效
        if (!STATUS_VALID.equals(strategy.getStrategyStatus())) {
            String errorMsg = "策略已失效（非有效状态）！strategyStatus=" + strategy.getStrategyStatus();
            log.error(errorMsg);
            result.setExecuteMsg(errorMsg);
            return null;
        }

        return strategy;
    }

    /**
     * 确定最终执行动作（入参action优先，否则用策略默认）
     * @return 有效动作码（无效返回null）
     */
    private Integer determineActionCode(Integer actionFromParam, BizLiftingStrategy strategy, StrategyExecuteResultVO result) {
        Integer actionCode = actionFromParam != null ? actionFromParam : strategy.getStrategyAction();

        // 校验动作编码有效性
        if (actionCode == null || (!ACTION_LIFT.equals(actionCode) && !ACTION_LOWER.equals(actionCode))) {
            String errorMsg = "策略动作无效｜actionCode=" + actionCode;
            log.error(errorMsg);
            result.setActionCode(actionCode);
            result.setActionDesc("未知");
            result.setExecuteMsg(errorMsg);
            return null;
        }

        return actionCode;
    }

    /**
     * 获取策略绑定的杆ID（单杆场景取第一个）
     */
    private Long getBindRodId(Long strategyId, StrategyExecuteResultVO result) {
        List<Long> rodIds = strategyRodService.getRodIdsByStrategyId(strategyId);
        if (CollectionUtils.isEmpty(rodIds)) {
            String errorMsg = "策略未绑定任何升降杆！strategyId=" + strategyId;
            log.error(errorMsg);
            result.setTotalRodCount(0);
            result.setExecuteMsg(errorMsg);
            return null;
        }

        Long rodId = rodIds.get(0);
        result.setTotalRodCount(rodIds.size());
        result.setRodId(rodId);

        // 查询杆基础信息
        BizLiftingRod rod = liftingRodService.getById(rodId);
        if (rod != null) {
            result.setRodName(rod.getRodName());
        }

        return rodId;
    }

    /**
     * 获取策略时间明细（取第一个明细）
     */
    private BizLiftingStrategyDetail getStrategyDetail(Long strategyId) {
        List<BizLiftingStrategyDetail> detailList = strategyDetailService.getByStrategyId(strategyId);
        return CollectionUtils.isEmpty(detailList) ? null : detailList.get(0);
    }

    /**
     * 处理Begin时间节点执行逻辑
     */
    private void handleBeginTimeExecute(BizLiftingStrategy strategy, BizLiftingStrategyDetail detail,
                                        Long rodId, Integer actionCode, StrategyExecuteResultVO result) {
        // 1. 校验杆有效性
        BizLiftingRod rod = checkRodValid(rodId, result);
        if (rod == null) {
            return;
        }

        // 2. 判断是否错过Begin时间
        boolean isMissBegin = isMissTime(detail.getDetailBegin());
        String missBeginMsg = isMissBegin ? "错过Begin时间" : "按时执行Begin操作";

        // 3. 执行杆操作（含重试）
        boolean executeSuccess = executeRodOperation(rod, rodId, actionCode, "Begin", missBeginMsg, result);

        // 4. 处理一次性策略状态（无End时间则标记已结束）
        if (STRATEGY_TYPE_ONCE.equals(strategy.getStrategyType()) && (detail.getDetailEnd() == null || detail.getDetailEnd().isEmpty())) {
            updateOnceStrategyStatus(strategy, executeSuccess, "Begin节点执行后（无End时间）");
        }
    }

    /**
     * 处理End时间节点执行逻辑
     */
    private void handleEndTimeExecute(BizLiftingStrategy strategy, BizLiftingStrategyDetail detail,
                                      Long rodId, Integer actionCode, StrategyExecuteResultVO result) {
        // 1. 校验杆有效性
        BizLiftingRod rod = checkRodValid(rodId, result);
        if (rod == null) {
            return;
        }

        // 2. 确定End节点动作（与Begin相反）
        Integer endActionCode = (actionCode == ACTION_LIFT) ? ACTION_LOWER : ACTION_LIFT;

        // 3. 判断是否错过End时间
        boolean isMissEnd = isMissTime(detail.getDetailEnd());
        String missEndMsg = isMissEnd ? "错过End时间" : "按时执行End操作";

        // 4. 执行杆操作（含重试）
        boolean executeSuccess = executeRodOperation(rod, rodId, endActionCode, "End", missEndMsg, result);

        // 5. 处理一次性策略状态（有End时间则标记已结束）
        if (STRATEGY_TYPE_ONCE.equals(strategy.getStrategyType())) {
            updateOnceStrategyStatus(strategy, executeSuccess, "End节点执行后");
        }
    }

    /**
     * 校验杆有效性（有效返回杆对象，无效返回null）
     */
    private BizLiftingRod checkRodValid(Long rodId, StrategyExecuteResultVO result) {
        BizLiftingRod rod = liftingRodService.getById(rodId);
        if (rod == null) {
            String skipMsg = "升降杆不存在，跳过执行！rodId=" + rodId;
            log.warn(skipMsg);
            result.setSkipCount(1);
            result.setExecuteMsg(skipMsg);
            return null;
        }

        if (!STATUS_VALID.equals(rod.getRodStatus())) {
            String skipMsg = "升降杆已失效（status=" + rod.getRodStatus() + "），跳过执行！rodId=" + rodId;
            log.warn(skipMsg);
            result.setSkipCount(1);
            result.setExecuteMsg(skipMsg);
            return null;
        }

        return rod;
    }

    /**
     * 执行杆操作（含离线重试）
     */
    private boolean executeRodOperation(BizLiftingRod rod, Long rodId, Integer actionCode,
                                        String timeNode, String missMsg, StrategyExecuteResultVO result) {
        String actionDesc = getActionDesc(actionCode);
        boolean executeSuccess = false;

        // 1. 杆在线状态判断
        Boolean isOnline = rod.getRodOffline();
        if (Boolean.TRUE.equals(isOnline)) {
            // 在线：直接执行
            try {
                liftingRodService.liftRod(rodId, actionCode, OPERATOR_DEFAULT);
                executeSuccess = true;
                String successMsg = String.format("[%s节点] %s：杆在线，%s成功，rodId=%s，名称=%s",
                        timeNode, missMsg, actionDesc, rodId, rod.getRodName());
                log.info(successMsg);
                result.setSuccessCount(1);
                result.setExecuteMsg(successMsg);
            } catch (Exception e) {
                String failMsg = String.format("[%s节点] %s：杆在线但%s失败，原因=%s，rodId=%s",
                        timeNode, missMsg, actionDesc, e.getMessage(), rodId);
                log.error(failMsg, e);
                result.setFailCount(1);
                result.setExecuteMsg(failMsg);
            }
        } else {
            // 离线：调用重试工具类
            log.warn("[{}节点] {}：杆离线，触发重试逻辑，rodId={}", timeNode, missMsg, rodId);

            executeSuccess = RodRetryUtil.executeWithRetry(rodId, actionCode, OPERATOR_DEFAULT, liftingRodService);

            if (executeSuccess) {
                String successMsg = String.format("[%s节点] %s：杆离线，重试后%s成功，rodId=%s，名称=%s",
                        timeNode, missMsg, actionDesc, rodId, rod.getRodName());
                log.info(successMsg);
                result.setSuccessCount(1);
                result.setExecuteMsg(successMsg);
            } else {
                String failMsg = String.format("[%s节点] %s：杆离线，重试后%s失败（重试次数用尽），rodId=%s，名称=%s",
                        timeNode, missMsg, actionDesc, rodId, rod.getRodName());
                log.error(failMsg);
                result.setFailCount(1);
                result.setExecuteMsg(failMsg);
            }
        }

        return executeSuccess;
    }

    /**
     * 更新一次性策略状态为已结束
     */
    private void updateOnceStrategyStatus(BizLiftingStrategy strategy, boolean executeSuccess, String reason) {
        if (!executeSuccess) {
            log.info("{}：执行失败，不更新一次性策略状态，strategyId={}", reason, strategy.getStrategyId());
            return;
        }

        try {
            strategy.setStrategyStatus(STRATEGY_STATUS_FINISHED);
            strategy.setStrategyUpdatetime(System.currentTimeMillis() / 1000);
            liftingStrategyService.updateById(strategy);

            String successMsg = String.format("%s：一次性策略已更新为已结束状态，strategyId=%s，名称=%s",
                    reason, strategy.getStrategyId(), strategy.getStrategyName());
            log.info(successMsg);
        } catch (Exception e) {
            String errorMsg = String.format("%s：更新一次性策略状态失败，reason=%s，strategyId=%s",
                    reason, e.getMessage(), strategy.getStrategyId());
            log.error(errorMsg, e);
        }
    }

    /**
     * 判断是否错过指定时间（允许30秒内的延迟，避免毫秒级误差误判）
     * @param timeStr 目标时间（HH:mm）
     * @return true=错过（超过30秒），false=未错过（30秒内）
     */
    private boolean isMissTime(String timeStr) {
        try {
            LocalTime targetTime = LocalTime.parse(timeStr, TIME_FORMATTER);
            LocalTime currentTime = LocalTime.now();

            // 计算目标时间到当前时间的总秒数差（正数=当前晚于目标，负数=当前早于目标）
            long secondsDiff = java.time.Duration.between(targetTime, currentTime).getSeconds();

            // 仅当延迟超过30秒（secondsDiff>30），才判定为“错过”；≤30秒均视为“按时”
            return secondsDiff > 30;
        } catch (Exception e) {
            log.error("解析时间异常，timeStr={}", timeStr, e);
            // 解析失败时默认判定为“未错过”，避免影响任务执行
            return false;
        }
    }

    /**
     * 获取动作描述
     */
    private String getActionDesc(Integer actionCode) {
        if (actionCode == null) {
            return "未知";
        }
        return actionCode == ACTION_LIFT ? "升杆" : "降杆";
    }

    /**
     * 打印最终执行结果（仅保留log日志，移除System.out）
     */
    private void printFinalResult(StrategyExecuteResultVO result) {
        log.info("=====================================");
        log.info("【升降杆定时任务】执行完成！");
        log.info("策略ID：{}，策略名称：{}", result.getStrategyId(), result.getStrategyName());
        log.info("执行杆ID：{}，杆名称：{}", result.getRodId(), result.getRodName());
        log.info("执行动作：{}（code={}）", result.getActionDesc(), result.getActionCode());
        log.info("执行结果：{}", result.getExecuteMsg());
        log.info("=====================================");
    }
}