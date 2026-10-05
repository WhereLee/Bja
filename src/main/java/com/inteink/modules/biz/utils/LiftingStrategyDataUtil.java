package com.inteink.modules.biz.utils;

import com.inteink.modules.biz.constant.LiftingStrategyConstant;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import java.util.ArrayList;
import java.util.List;

public class LiftingStrategyDataUtil {

    public static List<BizLiftingStrategyDetail> parseDetailTime(String detailTime) {
        List<BizLiftingStrategyDetail> detailList = new ArrayList<>();
        if (detailTime == null || detailTime.trim().isEmpty()) {
            return detailList;
        }

        BizLiftingStrategyDetail detail = new BizLiftingStrategyDetail();
        String[] timeArr = detailTime.split(LiftingStrategyConstant.TIME_SEPARATOR);
        if (timeArr.length == 1) {
            String singleTime = timeArr[0].trim();
            detail.setDetailBegin(singleTime);
            detail.setDetailEnd(null);
        } else if (timeArr.length == 2) {
            detail.setDetailBegin(timeArr[0].trim());
            detail.setDetailEnd(timeArr[1].trim());
        } else {
            return detailList;
        }

        boolean isValid = (detail.getDetailEnd() == null)
                ? LiftingStrategyCommonUtil.isValidTimeFormat(detail.getDetailBegin())
                : (LiftingStrategyCommonUtil.isValidTimeFormat(detail.getDetailBegin())
                && LiftingStrategyCommonUtil.isValidTimeFormat(detail.getDetailEnd()));

        if (isValid) {
            detailList.add(detail);
        }
        return detailList;
    }

    public static List<Long> parseRodIdsToLongList(String rodIds) {
        List<Long> rodIdList = new ArrayList<>();
        if (rodIds == null || rodIds.trim().isEmpty()) {
            return rodIdList;
        }
        String[] rodIdArr = rodIds.split(LiftingStrategyConstant.TIME_SEPARATOR);
        for (String rodIdStr : rodIdArr) {
            try {
                rodIdList.add(Long.parseLong(rodIdStr.trim()));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return rodIdList;
    }

    public static String formatTimePeriod(List<BizLiftingStrategyDetail> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return "无";
        }
        BizLiftingStrategyDetail first = detailList.get(0);
        if (first.getDetailEnd() == null || first.getDetailEnd().isEmpty()) {
            return first.getDetailBegin();
        } else {
            return first.getDetailBegin() + LiftingStrategyConstant.TIME_CONNECTOR + first.getDetailEnd();
        }
    }

    public static String formatRodInfo(List<LiftingRodVO> rodList) {
        if (rodList == null || rodList.isEmpty()) {
            return "无";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rodList.size(); i++) {
            LiftingRodVO rod = rodList.get(i);
            sb.append(rod.getRodId()).append("(").append(rod.getRodName()).append(")");
            if (i < rodList.size() - 1) {
                sb.append(LiftingStrategyConstant.ROD_INFO_SEPARATOR);
            }
        }
        return sb.toString();
    }
}