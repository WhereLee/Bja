package com.inteink.modules.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.model.vo.DeviceInfoVO;
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
     * 通用杆操作（手动/自动共用）：下发指令 + 更新状态；日志由 @BizLog 切面统一记录。
     *
     * @param type       手动/自动
     * @param strategyId 自动时来源策略（手动传 null）
     */
    void operateRod(Long rodId, Integer action, RodLogTypeEnum type, Long strategyId);

    /**
     * 对账：拉取该杆绑定设备的真实态，校正 rod_state / rod_offline。
     *
     * @return 设备探测视图（可能为 null，若无绑定转换器）
     */
    DeviceInfoVO reconcile(Long rodId);

    /** 批量对账：以设备快照真实态校正所有绑定杆，返回变更数。 */
    int reconcileAll();
}
