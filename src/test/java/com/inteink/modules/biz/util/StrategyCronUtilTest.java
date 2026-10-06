package com.inteink.modules.biz.util;

import com.inteink.common.exception.RRException;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.enums.StrategyTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * StrategyCronUtil 纯逻辑单测（不依赖 Spring/DB）。
 */
class StrategyCronUtilTest {

    private BizLiftingStrategy strategy(StrategyTypeEnum type, String dates) {
        BizLiftingStrategy s = new BizLiftingStrategy();
        s.setStrategyType(type.getCode());
        s.setStrategyDates(dates);
        return s;
    }

    @Test
    void 每日生成cron() {
        assertEquals("0 0 3 * * ?", StrategyCronUtil.build(strategy(StrategyTypeEnum.DAILY, null), "03:00"));
        assertEquals("0 30 7 * * ?", StrategyCronUtil.build(strategy(StrategyTypeEnum.DAILY, null), "07:30"));
    }

    @Test
    void 每周生成cron() {
        assertEquals("0 0 8 ? * 2,3,5", StrategyCronUtil.build(strategy(StrategyTypeEnum.WEEKLY, "2,3,5"), "08:00"));
    }

    @Test
    void 每月生成cron() {
        assertEquals("0 0 3 1,15 * ?", StrategyCronUtil.build(strategy(StrategyTypeEnum.MONTHLY, "1,15"), "03:00"));
    }

    @Test
    void 指定日期生成一次性cron带年份() {
        assertEquals("0 59 23 31 12 ? 2026", StrategyCronUtil.build(strategy(StrategyTypeEnum.APPOINT, "2026-12-31"), "23:59"));
    }

    @Test
    void 非法时间抛异常() {
        assertThrows(RRException.class, () -> StrategyCronUtil.build(strategy(StrategyTypeEnum.DAILY, null), "25:00"));
    }

    @Test
    void 每月缺日期抛异常() {
        assertThrows(RRException.class, () -> StrategyCronUtil.build(strategy(StrategyTypeEnum.MONTHLY, null), "03:00"));
    }
}
