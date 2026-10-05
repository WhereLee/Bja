package com.inteink.modules.biz.aspect;

import com.inteink.modules.biz.model.enums.BizLogKind;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.service.RodOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 杆动作日志处理器：从入参取 rodId/action/type/strategyId，按成功或异常写一行 rod_log。
 * 对应方法：LiftingRodService.operateRod(Long, Integer, RodLogTypeEnum, Long)。
 */
@Component
@RequiredArgsConstructor
public class RodOpLogHandler implements BizLogHandler {

    private final RodOperationLogService rodOperationLogService;

    @Override
    public BizLogKind kind() {
        return BizLogKind.ROD_OP;
    }

    @Override
    public void handle(BizLogContext context) {
        Object[] args = context.getJoinPoint().getArgs();
        if (args.length < 4) {
            return;
        }
        Long rodId = (Long) args[0];
        Integer action = (Integer) args[1];
        RodLogTypeEnum type = (RodLogTypeEnum) args[2];
        Long strategyId = (Long) args[3];
        rodOperationLogService.record(rodId, action, type, strategyId, context.success());
    }
}
