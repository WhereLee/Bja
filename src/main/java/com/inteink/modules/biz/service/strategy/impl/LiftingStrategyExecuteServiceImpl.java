package com.inteink.modules.biz.service.strategy.impl;

import com.inteink.modules.biz.annotation.*;

import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import com.inteink.modules.biz.model.vo.StrategyExecuteResultVO;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
import com.inteink.modules.biz.service.strategy.AbstractLiftingStrategyService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * 升降策略执行服务实现类
 * 修复版：保留AOP + 解决getRodVOById方法不存在问题 + 还原核心逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiftingStrategyExecuteServiceImpl extends AbstractLiftingStrategyService implements LiftingStrategyExecuteService {

    // 常量抽取：消除魔法值
    private static final Long STATUS_VALID = 0L;       // 策略/升降杆有效状态
    private static final Integer ACTION_LIFT = 1;      // 升杆动作编码
    private static final Integer ACTION_LOWER = 2;     // 降杆动作编码
    private static final Long OPERATOR_DEFAULT = 0L;   // 默认操作人
    private static final String ACTION_DESC_LIFT = "升杆";
    private static final String ACTION_DESC_LOWER = "降杆";
    private static final String ACTION_DESC_UNKNOWN = "未知";

    // 核心依赖注入
    private final LiftingStrategyService liftingStrategyService;
    private final BizLiftingStrategyRodService strategyRodService;
    private final LiftingRodService liftingRodService;

    @Override
    // 保留所有现有AOP注解，无改动
    @StrategyOperLog(logType = BizLiftingStrategyEnum.LOG_TYPE_EXECUTE)
    @StrategyLifeCycleCheck
    @StrategyExceptionHandler
    @StrategyLogRecord
    @StrategyTimeCost(threshold = 1000)
    public StrategyExecuteResultVO execute(Long strategyId, Integer action) {
        // 1. 初始化返回结果
        StrategyExecuteResultVO result = initExecuteResult(strategyId);

        try {
            // 2. 查询策略 + 精准校验
            BizLiftingStrategy strategy = getStrategyById(strategyId);
            if (strategy == null) {
                String errorMsg = buildMsg("策略不存在", strategyId);
                log.error("【升降策略执行】{}", errorMsg);
                result.setExecuteMsg(errorMsg);
                return result;
            }
            if (!STATUS_VALID.equals(strategy.getStrategyStatus())) {
                String errorMsg = buildMsg("策略已失效（非有效状态）", strategyId);
                log.error("【升降策略执行】{}", errorMsg);
                result.setExecuteMsg(errorMsg);
                return result;
            }
            result.setStrategyName(strategy.getStrategyName());

            // 3. 确定执行动作 + 有效性校验
            Integer executeAction = action == null ? strategy.getStrategyAction() : action;
            if (executeAction == null || (!ACTION_LIFT.equals(executeAction) && !ACTION_LOWER.equals(executeAction))) {
                String errorMsg = buildMsg("策略动作无效｜actionCode={}", strategyId, executeAction);
                log.error("【升降策略执行】{}", errorMsg);
                result.setActionCode(executeAction);
                result.setActionDesc(ACTION_DESC_UNKNOWN);
                result.setExecuteMsg(errorMsg);
                return result;
            }
            String actionDesc = ACTION_LIFT.equals(executeAction) ? ACTION_DESC_LIFT : ACTION_DESC_LOWER;
            result.setActionCode(executeAction);
            result.setActionDesc(actionDesc);

            // 4. 查询杆列表（完全基于现有方法）
            List<LiftingRodVO> rodList = liftingRodService.getValidRodListByStrategyId(strategyId);
            result.setTotalRodCount(rodList.size());

            if (CollectionUtils.isEmpty(rodList)) {
                String errorMsg = buildMsg("策略未绑定任何升降杆", strategyId);
                log.error("【升降策略执行】{}", errorMsg);
                result.setExecuteMsg(errorMsg);
                return result;
            }
            log.info("【升降策略执行】策略基本信息｜strategyId={}｜策略名称={}｜执行动作={}｜绑定杆数量={}",
                    strategyId, strategy.getStrategyName(), actionDesc, rodList.size());

            // 5. 遍历单杆执行（基于现有LiftingRodVO，无新增方法）
            executeSingleRodOperation(rodList, result);

            // 6. 组装标准化结果
            assembleExecuteResultMsg(result, rodList.size());

        } catch (Exception e) {
            String errorMsg = buildMsg("策略执行整体异常", strategyId);
            log.error("【升降策略执行】{}", errorMsg, e);
            result.setExecuteMsg(errorMsg);
        }

        return result;
    }

    /**
     * 初始化执行结果对象
     */
    private StrategyExecuteResultVO initExecuteResult(Long strategyId) {
        StrategyExecuteResultVO result = new StrategyExecuteResultVO();
        result.setStrategyId(strategyId);
        result.setSuccessCount(0);
        result.setFailCount(0);
        result.setSkipCount(0);
        return result;
    }

    /**
     * 遍历执行单杆操作（完全基于现有LiftingRodVO，解决方法不存在问题）
     */
    private void executeSingleRodOperation(List<LiftingRodVO> rodList, StrategyExecuteResultVO result) {
        Integer actionCode = result.getActionCode();
        String actionDesc = result.getActionDesc();
        Long strategyId = result.getStrategyId();

        for (LiftingRodVO rodVO : rodList) {
            Long rodId = rodVO.getRodId();
            try {
                // 1. 校验杆状态（基于现有VO的rodStatus字段）
                if (!STATUS_VALID.equals(rodVO.getRodStatus())) {
                    log.warn("【升降策略执行】跳过｜strategyId={}｜rodId={}｜杆名称={}｜原因=升降杆已失效",
                            strategyId, rodId, rodVO.getRodName());
                    result.setSkipCount(result.getSkipCount() + 1);
                    continue;
                }

                // 2. 执行杆操作（完全调用现有controlRod方法）
                boolean executeSuccess = liftingRodService.controlRod(rodId, actionCode);
                if (executeSuccess) {
                    // 若项目中有liftRod方法则保留，无则注释（二选一即可）
                    // liftingRodService.liftRod(rodId, actionCode, OPERATOR_DEFAULT);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                    log.info("【升降策略执行】成功｜strategyId={}｜rodId={}｜杆名称={}｜执行动作={}",
                            strategyId, rodId, rodVO.getRodName(), actionDesc);
                } else {
                    result.setFailCount(result.getFailCount() + 1);
                    log.warn("【升降策略执行】失败｜strategyId={}｜rodId={}｜杆名称={}｜执行动作={}｜原因=controlRod返回失败",
                            strategyId, rodId, rodVO.getRodName(), actionDesc);
                }

            } catch (Exception e) {
                // 单杆异常兜底：仅记录失败，不阻断其他杆
                log.error("【升降策略执行】失败｜strategyId={}｜rodId={}｜杆名称={}｜执行动作={}｜原因={}",
                        strategyId, rodId, rodVO.getRodName(), actionDesc, e.getMessage(), e);
                result.setFailCount(result.getFailCount() + 1);
            }
        }
    }

    /**
     * 组装标准化执行结果信息
     */
    private void assembleExecuteResultMsg(StrategyExecuteResultVO result, int totalRodCount) {
        String executeMsg = String.format(
                "执行动作：%s，绑定杆总数：%s，成功执行：%s根，失败：%s根，跳过：%s根",
                result.getActionDesc(), totalRodCount,
                result.getSuccessCount(), result.getFailCount(), result.getSkipCount()
        );
        result.setExecuteMsg(executeMsg);
        log.info("【升降策略执行】汇总｜{}", executeMsg);
    }

    /**
     * 构建标准化日志/返回信息
     */
    private String buildMsg(String baseMsg, Long strategyId, Object... params) {
        String baseFormat = "%s！strategyId=%s";
        if (params != null && params.length > 0) {
            baseFormat += "｜" + String.join("｜", new String[params.length]).replace("\0", "%s");
        }
        return String.format(baseFormat, baseMsg, strategyId, params);
    }
}