package com.inteink.modules.biz.service.impl;

import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;
import com.inteink.modules.biz.mapper.BizLiftingStrategyRodMapper;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyRod;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.service.LiftingRodService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * END 前置状态守卫单测：杆非预期态则跳过、处于预期态才反向。
 */
class StrategyExecuteServiceImplTest {

    private StrategyExecuteServiceImpl impl(BizLiftingStrategyMapper sm,
                                            BizLiftingStrategyRodMapper rm,
                                            LiftingRodService lrs) {
        return new StrategyExecuteServiceImpl(sm, rm, lrs);
    }

    private BizLiftingStrategy strategy() {
        BizLiftingStrategy st = new BizLiftingStrategy();
        st.setStrategyId(1L);
        st.setStrategyAction(1); // begin 期望把杆置为“升”
        st.setStrategyStatus(0L);
        return st;
    }

    private void wire(BizLiftingStrategyMapper sm, BizLiftingStrategyRodMapper rm) {
        when(sm.selectById(1L)).thenReturn(strategy());
        BizLiftingStrategyRod rel = new BizLiftingStrategyRod();
        rel.setRodId(10L);
        when(rm.selectList(any())).thenReturn(List.of(rel));
    }

    @Test
    void END在杆非预期态则跳过不下发() {
        BizLiftingStrategyMapper sm = mock(BizLiftingStrategyMapper.class);
        BizLiftingStrategyRodMapper rm = mock(BizLiftingStrategyRodMapper.class);
        LiftingRodService lrs = mock(LiftingRodService.class);
        wire(sm, rm);
        BizLiftingRod rod = new BizLiftingRod();
        rod.setRodId(10L);
        rod.setRodState(0); // 默认，未处于“升”
        when(lrs.getById(10L)).thenReturn(rod);

        impl(sm, rm, lrs).executeByStrategy(1L, 2, "END");
        verify(lrs, never()).operateRod(any(), any(), any(RodLogTypeEnum.class), any());
    }

    @Test
    void END在杆处于预期态则执行反向() {
        BizLiftingStrategyMapper sm = mock(BizLiftingStrategyMapper.class);
        BizLiftingStrategyRodMapper rm = mock(BizLiftingStrategyRodMapper.class);
        LiftingRodService lrs = mock(LiftingRodService.class);
        wire(sm, rm);
        BizLiftingRod rod = new BizLiftingRod();
        rod.setRodId(10L);
        rod.setRodState(1); // 升，符合 begin 期望态
        when(lrs.getById(10L)).thenReturn(rod);

        impl(sm, rm, lrs).executeByStrategy(1L, 2, "END");
        verify(lrs, times(1)).operateRod(eq(10L), eq(2), eq(RodLogTypeEnum.AUTO), eq(1L));
    }
}
