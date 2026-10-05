package com.inteink.modules.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.model.vo.LiftingRodVO;

/**
 * 道闸杆服务：杆的增删改查 + 手动升降。
 */
public interface LiftingRodService extends IService<BizLiftingRod> {

    Long saveRod(BizLiftingRod rod, Long operator);

    void updateRod(BizLiftingRod rod);

    /** 逻辑删除（rod_status = rodId） */
    void removeRod(Long rodId);

    PageUtils queryPage(LiftingRodForm form);

    LiftingRodVO detail(Long rodId);

    /**
     * 手动升降：下发指令 + 更新状态 + 记日志。
     *
     * @param rodId    杆ID
     * @param action   1-升 2-降
     * @param operator 操作人
     */
    void manualOperate(Long rodId, Integer action, Long operator);
}
