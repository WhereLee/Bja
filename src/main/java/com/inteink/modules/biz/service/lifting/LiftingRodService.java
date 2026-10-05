package com.inteink.modules.biz.service.lifting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.model.vo.LiftingRodVO;

import java.util.List;

public interface LiftingRodService extends IService<BizLiftingRod> {

    void liftRod(Long rodId, Integer action, Long operator);

    void liftRod(Long rodId, Integer action);

    PageUtils queryLogPage(LiftingRodForm form, Long userId);

    PageUtils queryRodPage(LiftingRodForm form);

    List<LiftingRodVO> getRodDetailListByIds(List<Long> rodIds);

    default List<LiftingRodVO> getRodListByIds(List<Long> rodIds) {
        return this.getRodDetailListByIds(rodIds);
    }

    List<LiftingRodVO> getValidRodListByStrategyId(Long strategyId);

    boolean controlRod(Long rodId, Integer action);
}