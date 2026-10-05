package com.inteink.modules.biz.assembler;

import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * 策略日志备注装配器
 * 统一管理新增/修改/审核/删除策略的日志备注格式，核心字段复用
 */
@Component
public class StrategyLogAssembler {

    // 通用时间格式化器（与新增策略日志对齐）
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    /**
     * 【通用方法】组装策略核心日志字段（strategyId+策略名+格式化时间）
     * 供新增/审核/修改日志复用
     */
    public static String buildStrategyCoreLog(BizLiftingStrategy strategy) {
        // 1. 拆分策略名
        String[] nameSplit = strategy.getStrategyName().split("_");
        String pureName = nameSplit.length > 0 ? nameSplit[0] : strategy.getStrategyName();
        String timestampStr = nameSplit.length > 1 ? nameSplit[1] : "";

        // 2. 时间戳转格式化时间
        String formatTime = "";
        if (!timestampStr.isEmpty()) {
            try {
                long timestamp = Long.parseLong(timestampStr);
                formatTime = DATE_TIME_FORMATTER.format(Instant.ofEpochSecond(timestamp));
            } catch (NumberFormatException e) {
                formatTime = timestampStr; // 非数字时间戳保留原值
            }
        }

        // 3. 组装核心字段（对齐新增日志格式）
        return String.format("strategyId=%s,策略名=%s,时间=%s",
                strategy.getStrategyId(), pureName, formatTime);
    }

    /**
     * 构建修改策略日志备注（仅保留与新增一致的核心字段，删除冗余）
     * 注：该方法为兼容旧调用保留，实际推荐使用 buildUpdateLogExtRemark
     */
    public static String buildUpdateLogRemark(StrategyUpdateDTO updateDTO,
                                              BizLiftingStrategy oldStrategy,
                                              BizLiftingStrategy newStrategy,
                                              int oldDetailCount,
                                              int newDetailCount,
                                              int oldRodCount,
                                              int newRodCount) {
        // 仅保留与新增一致的核心模板，删除原明细/杆数量/新旧动作类型等冗余字段
        String coreLog = buildStrategyCoreLog(oldStrategy);
        return String.format(
                "修改策略：%s,strategyAction=%s,strategyRemark=%s，操作人=%s，修改后送往审核（状态：待审核/有效）。",
                coreLog,
                BizLiftingStrategyEnum.getActionDesc(newStrategy.getStrategyAction()),
                StringUtils.isBlank(newStrategy.getStrategyRemark()) ? "无" : newStrategy.getStrategyRemark(),
                updateDTO.getStrategyUpdater()
        );
    }

    /**
     * 构建审核策略日志备注（优化格式+复用核心字段+新增驳回原因）
     */
    public static String buildAuditLogRemark(BizLiftingStrategy strategy, StrategyAuditDTO auditDTO, String jobId, String reason) {
        // 1. 复用核心字段（strategyId+策略名+格式化时间）
        String coreLog = buildStrategyCoreLog(strategy);
        // 2. 审核结果描述（兜底未知）
        String checkResultDesc = BizLiftingStrategyEnum.getCheckStateDesc(auditDTO.getCheckState());
        if (StringUtils.isBlank(checkResultDesc)) {
            checkResultDesc = "未知";
        }
        // 3. 组装结构化日志（对齐新增格式）
        StringBuilder remark = new StringBuilder();
        remark.append("策略审核：").append(coreLog)
                .append("，审核人ID=").append(auditDTO.getOperatorId())
                .append("，审核结果=").append(checkResultDesc)
                .append("，驳回原因=").append(StringUtils.isBlank(reason) ? "无" : reason);
        // 定时任务ID（有值则补充）
        if (StringUtils.isNotBlank(jobId)) {
            remark.append("，定时任务ID=").append(jobId);
        }
        return remark.toString();
    }

    /**
     * 【核心】构建修改日志备注（完全对齐新增日志格式，含杆信息+执行时段）
     * 供修改侧Service调用，替代冗余的 buildUpdateLogRemark
     */
    public static String buildUpdateLogExtRemark(StrategyUpdateDTO updateDTO,
                                                 BizLiftingStrategy newStrategy,
                                                 String rodInfo,
                                                 String timePeriod) {
        String coreLog = buildStrategyCoreLog(newStrategy);
        // 完全复用新增侧的日志模板，仅将“新增”改为“修改”
        return String.format(
                "修改策略：%s,strategyAction=%s,strategyRemark=%s，绑定杆ID=%s，执行时段=%s，操作人=%s，修改后送往审核（状态：待审核/有效）。",
                coreLog,
                BizLiftingStrategyEnum.getActionDesc(newStrategy.getStrategyAction()),
                StringUtils.isBlank(newStrategy.getStrategyRemark()) ? "无" : newStrategy.getStrategyRemark(),
                rodInfo,
                timePeriod,
                updateDTO.getStrategyUpdater()
        );
    }
}