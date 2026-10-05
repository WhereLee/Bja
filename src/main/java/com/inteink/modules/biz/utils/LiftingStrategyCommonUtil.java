package com.inteink.modules.biz.utils;

import com.inteink.modules.biz.constant.LiftingStrategyConstant;
import java.time.Instant;

public class LiftingStrategyCommonUtil {

    public static String generateUniqueStrategyName(String coreName) {
        if (coreName == null || coreName.trim().isEmpty()) {
            coreName = LiftingStrategyConstant.DEFAULT_STRATEGY_NAME;
        }
        long timestamp = Instant.now().getEpochSecond();
        return coreName.trim() + "_" + timestamp;
    }

    public static boolean isValidTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }
        return time.trim().matches(LiftingStrategyConstant.TIME_FORMAT_REGEX);
    }

    public static String formatLogContent(String msg, Long strategyId) {
        return String.format(LiftingStrategyConstant.BASE_LOG_FORMAT, msg, strategyId);
    }

    public static long getCurrentTimestamp() {
        return Instant.now().getEpochSecond();
    }

    public static String getEmptyValueDefault(String value) {
        return value == null ? LiftingStrategyConstant.EMPTY_REMARK : value.trim();
    }
}