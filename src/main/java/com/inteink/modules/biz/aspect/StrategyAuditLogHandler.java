package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.mapper.BizLiftingStrategyLogMapper;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.enums.BizLogKind;
import com.inteink.modules.biz.model.enums.StrategyLogTypeEnum;
import com.inteink.modules.biz.model.form.StrategyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 策略审核/操作日志处理器：仅方法成功时记一条轨迹。
 * 依据注解 type：CREATE(返回值=新ID) / UPDATE(args[0]=StrategyForm) / AUDIT(args= id,pass,remark)。
 */
@Component
@RequiredArgsConstructor
public class StrategyAuditLogHandler implements BizLogHandler {

    private final BizLiftingStrategyLogMapper logMapper;

    @Override
    public BizLogKind kind() {
        return BizLogKind.STRATEGY_AUDIT;
    }

    @Override
    public void handle(BizLogContext context) {
        if (!context.success()) {
            return;
        }
        String type = context.getAnnotation().type();
        Object[] args = context.getJoinPoint().getArgs();
        Long strategyId;
        Integer logType;
        String remark;
        switch (type) {
            case "CREATE":
                strategyId = (Long) context.getResult();
                logType = StrategyLogTypeEnum.CREATE.getCode();
                remark = "新增";
                break;
            case "UPDATE":
                strategyId = args.length > 0 && args[0] instanceof StrategyForm
                        ? ((StrategyForm) args[0]).getStrategyId() : null;
                logType = StrategyLogTypeEnum.UPDATE.getCode();
                remark = "修改";
                break;
            case "AUDIT":
                strategyId = args.length > 0 ? (Long) args[0] : null;
                boolean pass = args.length > 1 && Boolean.TRUE.equals(args[1]);
                logType = pass ? StrategyLogTypeEnum.AUDIT_PASS.getCode() : StrategyLogTypeEnum.AUDIT_REJECT.getCode();
                remark = args.length > 2 && args[2] != null ? (String) args[2] : (pass ? "审核通过" : "审核驳回");
                break;
            default:
                return;
        }
        if (strategyId == null) {
            return;
        }
        BizLiftingStrategyLog log = new BizLiftingStrategyLog();
        log.setStrategyId(strategyId);
        log.setLogType(logType);
        log.setLogRemark(remark);
        log.setLogOperator(context.getOperator());
        log.setLogOperateTime(System.currentTimeMillis() / 1000);
        logMapper.insert(log);
    }
}
