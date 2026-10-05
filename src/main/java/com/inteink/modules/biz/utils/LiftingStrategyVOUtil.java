package com.inteink.modules.biz.utils;

import com.inteink.modules.biz.constant.LiftingStrategyConstant;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import java.util.ArrayList;
import java.util.List;

public class LiftingStrategyVOUtil {

    public static StrategyResponseVO buildBaseVO(BizLiftingStrategy strategy) {
        StrategyResponseVO vo = new StrategyResponseVO();
        vo.setStrategyId(strategy.getStrategyId());
        vo.setStrategyName(strategy.getStrategyName());
        vo.setStrategyAction(strategy.getStrategyAction());
        vo.setStrategyType(strategy.getStrategyType());
        vo.setStrategyDates(strategy.getStrategyDates());
        vo.setStrategyRemark(strategy.getStrategyRemark());
        vo.setStrategyCheckState(strategy.getStrategyCheckState());
        vo.setStrategyStatus(strategy.getStrategyStatus());
        vo.setRodList(new ArrayList<>());
        vo.setDetailList(new ArrayList<>());
        return vo;
    }

    public static void supplementVODesc(StrategyResponseVO vo) {
        if (vo.getStrategyCheckState() != null) {
            String checkDesc = BizLiftingStrategyEnum.getCheckStateDesc(vo.getStrategyCheckState());
            vo.setStrategyCheckStateDesc(checkDesc);
        } else {
            vo.setStrategyCheckStateDesc(LiftingStrategyConstant.UNKNOWN_DESC);
        }

        if (vo.getStrategyStatus() != null) {
            String statusDesc = BizLiftingStrategyEnum.getStrategyStatusDesc(vo.getStrategyStatus());
            vo.setStrategyStatusDesc(statusDesc);
        } else {
            vo.setStrategyStatusDesc(LiftingStrategyConstant.UNKNOWN_DESC);
        }

        if (vo.getStrategyStatus() != null && vo.getStrategyStatus().equals(LiftingStrategyConstant.STRATEGY_STATUS_VALID)) {
            vo.setStrategyStatusDesc(LiftingStrategyConstant.VALID_STATUS_DESC);
        }
    }

    public static void fixVOEmptyList(StrategyResponseVO vo) {
        if (vo.getRodList() == null) {
            vo.setRodList(new ArrayList<>());
        }
        if (vo.getDetailList() == null) {
            vo.setDetailList(new ArrayList<>());
        }
    }

    public static StrategyResponseVO assembleCompleteVO(BizLiftingStrategy strategy,
                                                        List<BizLiftingStrategyDetail> detailList,
                                                        List<Long> rodIds) {
        StrategyResponseVO vo = buildBaseVO(strategy);
        vo.setDetailList(detailList == null ? new ArrayList<>() : detailList);
        supplementVODesc(vo);
        fixVOEmptyList(vo);
        return vo;
    }

    public static StrategyResponseVO assembleVOWithRodList(BizLiftingStrategy strategy,
                                                           List<BizLiftingStrategyDetail> detailList,
                                                           List<LiftingRodVO> rodList) {
        StrategyResponseVO vo = buildBaseVO(strategy);
        vo.setDetailList(detailList == null ? new ArrayList<>() : detailList);
        vo.setRodList(rodList == null ? new ArrayList<>() : rodList);
        supplementVODesc(vo);
        fixVOEmptyList(vo);
        return vo;
    }
}