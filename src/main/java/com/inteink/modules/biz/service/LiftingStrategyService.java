package com.inteink.modules.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.form.StrategyForm;
import com.inteink.modules.biz.model.form.StrategyQueryForm;
import com.inteink.modules.biz.model.vo.LiftingStrategyVO;

import java.util.List;

/**
 * 升降策略服务：CRUD + 审核(通过后生成定时) + 暂停/恢复 + 删除 + 立即执行 + 审核日志。
 */
public interface LiftingStrategyService extends IService<BizLiftingStrategy> {

    Long saveStrategy(StrategyForm form, Long operator);

    void updateStrategy(StrategyForm form, Long operator);

    /** 审核：pass=true 通过并生成定时任务，false 驳回 */
    void audit(Long strategyId, boolean pass, String remark, Long operator);

    void removeStrategy(Long strategyId);

    void pause(Long strategyId);

    void resume(Long strategyId);

    int executeNow(Long strategyId);

    PageUtils queryPage(StrategyQueryForm form);

    LiftingStrategyVO detail(Long strategyId);

    List<BizLiftingStrategyLog> auditLogs(Long strategyId);
}
