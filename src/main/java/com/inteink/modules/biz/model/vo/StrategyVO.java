package com.inteink.modules.biz.model.vo;

import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import lombok.Data;

import java.util.List;

/**
 * 策略新增/修改VO（封装主表+明细）
 */
@Data
public class StrategyVO {
    // 主表数据
    private BizLiftingStrategy strategy;
    // 明细列表
    private List<BizLiftingStrategyDetail> detailList;

    // 分页参数（查询时用）
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 筛选参数（查询时用）
    private String strategyName; // 策略名称模糊查询
    private Integer strategyAction; // 动作：1升/2降
    private Integer strategyType; // 类型：1每日/2每周/3每月/4指定日期

    // 新增：关联的升降杆ID列表
    private List<Long> rodIds;
}