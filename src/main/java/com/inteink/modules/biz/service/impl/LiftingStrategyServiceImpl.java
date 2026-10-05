package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.exception.RRException;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.StringUtils;
import com.inteink.modules.biz.factory.StrategyScheduleFactory;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.mapper.BizLiftingStrategyDetailMapper;
import com.inteink.modules.biz.mapper.BizLiftingStrategyLogMapper;
import com.inteink.modules.biz.annotation.BizLog;
import com.inteink.modules.biz.annotation.TimeCost;
import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;
import com.inteink.modules.biz.mapper.BizLiftingStrategyRodMapper;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyRod;
import com.inteink.modules.biz.model.enums.BizLogKind;
import com.inteink.modules.biz.model.enums.RodActionEnum;
import com.inteink.modules.biz.model.enums.StrategyCheckStateEnum;
import com.inteink.modules.biz.model.enums.StrategyTypeEnum;
import com.inteink.modules.biz.model.form.StrategyForm;
import com.inteink.modules.biz.model.form.StrategyQueryForm;
import com.inteink.modules.biz.model.vo.LiftingStrategyVO;
import com.inteink.modules.biz.service.LiftingStrategyService;
import com.inteink.modules.biz.service.StrategyExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiftingStrategyServiceImpl extends ServiceImpl<BizLiftingStrategyMapper, BizLiftingStrategy>
        implements LiftingStrategyService {

    private static final long STATUS_VALID = 0L;

    private final BizLiftingStrategyDetailMapper detailMapper;
    private final BizLiftingStrategyRodMapper relationMapper;
    private final BizLiftingStrategyLogMapper logMapper;
    private final BizLiftingRodMapper rodMapper;
    private final StrategyScheduleFactory scheduleFactory;
    private final StrategyExecuteService strategyExecuteService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @BizLog(kind = BizLogKind.STRATEGY_AUDIT, type = "CREATE")
    public Long saveStrategy(StrategyForm form, Long operator) {
        validateForm(form);
        Integer dup = this.count(new LambdaQueryWrapper<BizLiftingStrategy>()
                .eq(BizLiftingStrategy::getStrategyName, form.getStrategyName())
                .eq(BizLiftingStrategy::getStrategyStatus, STATUS_VALID));
        if (dup != null && dup > 0) {
            throw new RRException("同名策略已存在");
        }
        long now = System.currentTimeMillis() / 1000;
        BizLiftingStrategy strategy = new BizLiftingStrategy();
        strategy.setStrategyName(form.getStrategyName());
        strategy.setStrategyAction(form.getStrategyAction());
        strategy.setStrategyType(form.getStrategyType());
        strategy.setStrategyDates(form.getStrategyDates());
        strategy.setStrategyRemark(form.getStrategyRemark());
        strategy.setStrategyCheckState(StrategyCheckStateEnum.PENDING.getCode());
        strategy.setStrategyCreator(operator);
        strategy.setStrategyCreatetime(now);
        strategy.setStrategyUpdatetime(now);
        strategy.setStrategyStatus(STATUS_VALID);
        this.save(strategy);

        resetDetail(strategy.getStrategyId(), form);
        resetRelations(strategy.getStrategyId(), form.getRodIds());
        return strategy.getStrategyId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @BizLog(kind = BizLogKind.STRATEGY_AUDIT, type = "UPDATE")
    public void updateStrategy(StrategyForm form, Long operator) {
        BizLiftingStrategy strategy = getValidStrategy(form.getStrategyId());
        validateForm(form);
        strategy.setStrategyName(form.getStrategyName());
        strategy.setStrategyAction(form.getStrategyAction());
        strategy.setStrategyType(form.getStrategyType());
        strategy.setStrategyDates(form.getStrategyDates());
        strategy.setStrategyRemark(form.getStrategyRemark());
        strategy.setStrategyUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(strategy);

        resetDetail(strategy.getStrategyId(), form);
        resetRelations(strategy.getStrategyId(), form.getRodIds());
        // 已通过的策略，改动后刷新其定时任务
        if (StrategyCheckStateEnum.PASS.getCode().equals(strategy.getStrategyCheckState())) {
            scheduleFactory.createJobs(strategy, getDetail(strategy.getStrategyId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TimeCost("策略审核")
    @BizLog(kind = BizLogKind.STRATEGY_AUDIT, type = "AUDIT")
    public void audit(Long strategyId, boolean pass, String remark, Long operator) {
        BizLiftingStrategy strategy = getValidStrategy(strategyId);
        if (!StrategyCheckStateEnum.PENDING.getCode().equals(strategy.getStrategyCheckState())) {
            throw new RRException("该策略已审核，不能重复审核");
        }
        long now = System.currentTimeMillis() / 1000;
        strategy.setStrategyCheckState(pass
                ? StrategyCheckStateEnum.PASS.getCode() : StrategyCheckStateEnum.REJECT.getCode());
        strategy.setStrategyCheckUser(operator);
        strategy.setStrategyCheckTime(now);
        strategy.setStrategyUpdatetime(now);
        this.updateById(strategy);

        if (pass) {
            scheduleFactory.createJobs(strategy, getDetail(strategyId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeStrategy(Long strategyId) {
        BizLiftingStrategy strategy = getValidStrategy(strategyId);
        scheduleFactory.removeJobs(strategyId);
        relationMapper.delete(new LambdaQueryWrapper<BizLiftingStrategyRod>()
                .eq(BizLiftingStrategyRod::getStrategyId, strategyId));
        detailMapper.delete(new LambdaQueryWrapper<BizLiftingStrategyDetail>()
                .eq(BizLiftingStrategyDetail::getStrategyId, strategyId));
        strategy.setStrategyStatus(strategyId);
        strategy.setStrategyUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(strategy);
    }

    @Override
    public void pause(Long strategyId) {
        getValidStrategy(strategyId);
        scheduleFactory.pauseJobs(strategyId);
    }

    @Override
    public void resume(Long strategyId) {
        getValidStrategy(strategyId);
        scheduleFactory.resumeJobs(strategyId);
    }

    @Override
    public int executeNow(Long strategyId) {
        getValidStrategy(strategyId);
        return strategyExecuteService.executeByStrategy(strategyId);
    }

    @Override
    public PageUtils queryPage(StrategyQueryForm form) {
        Page<BizLiftingStrategy> page = new Page<>(form.getPageNum(), form.getPageSize());
        Page<BizLiftingStrategy> result = this.page(page, new LambdaQueryWrapper<BizLiftingStrategy>()
                .eq(BizLiftingStrategy::getStrategyStatus, STATUS_VALID)
                .like(StringUtils.isNotBlank(form.getStrategyName()), BizLiftingStrategy::getStrategyName, form.getStrategyName())
                .eq(form.getStrategyType() != null, BizLiftingStrategy::getStrategyType, form.getStrategyType())
                .eq(form.getCheckState() != null, BizLiftingStrategy::getStrategyCheckState, form.getCheckState())
                .orderByDesc(BizLiftingStrategy::getStrategyId));
        List<LiftingStrategyVO> vos = result.getRecords().stream().map(s -> {
            LiftingStrategyVO vo = buildVO(s);
            fillDetailAndRods(vo, s.getStrategyId());
            return vo;
        }).collect(Collectors.toList());
        return new PageUtils(vos, (int) result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public LiftingStrategyVO detail(Long strategyId) {
        BizLiftingStrategy strategy = getValidStrategy(strategyId);
        LiftingStrategyVO vo = buildVO(strategy);
        fillDetailAndRods(vo, strategyId);
        return vo;
    }

    @Override
    public List<BizLiftingStrategyLog> auditLogs(Long strategyId) {
        return logMapper.selectList(new LambdaQueryWrapper<BizLiftingStrategyLog>()
                .eq(BizLiftingStrategyLog::getStrategyId, strategyId)
                .orderByDesc(BizLiftingStrategyLog::getLogOperateTime));
    }

    // ===== 内部辅助 =====

    private BizLiftingStrategy getValidStrategy(Long strategyId) {
        if (strategyId == null) {
            throw new RRException("策略ID不能为空");
        }
        BizLiftingStrategy strategy = this.getById(strategyId);
        if (strategy == null || !strategy.isValid()) {
            throw new RRException("策略不存在或已删除");
        }
        return strategy;
    }

    private void validateForm(StrategyForm form) {
        if (StringUtils.isBlank(form.getStrategyName())) {
            throw new RRException("策略名称不能为空");
        }
        if (!RodActionEnum.isValid(form.getStrategyAction())) {
            throw new RRException("策略动作非法，只能 1-升 2-降");
        }
        boolean typeOk = false;
        for (StrategyTypeEnum e : StrategyTypeEnum.values()) {
            if (e.getCode().equals(form.getStrategyType())) {
                typeOk = true;
                break;
            }
        }
        if (!typeOk) {
            throw new RRException("策略类型非法");
        }
        if (StringUtils.isBlank(form.getDetailBegin())) {
            throw new RRException("开始时间不能为空");
        }
    }

    private void resetDetail(Long strategyId, StrategyForm form) {
        detailMapper.delete(new LambdaQueryWrapper<BizLiftingStrategyDetail>()
                .eq(BizLiftingStrategyDetail::getStrategyId, strategyId));
        BizLiftingStrategyDetail detail = new BizLiftingStrategyDetail();
        detail.setStrategyId(strategyId);
        detail.setDetailBegin(form.getDetailBegin());
        detail.setDetailEnd(form.getDetailEnd());
        detailMapper.insert(detail);
    }

    private BizLiftingStrategyDetail getDetail(Long strategyId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<BizLiftingStrategyDetail>()
                .eq(BizLiftingStrategyDetail::getStrategyId, strategyId)
                .last("LIMIT 1"));
    }

    private void resetRelations(Long strategyId, List<Long> rodIds) {
        relationMapper.delete(new LambdaQueryWrapper<BizLiftingStrategyRod>()
                .eq(BizLiftingStrategyRod::getStrategyId, strategyId));
        if (rodIds == null || rodIds.isEmpty()) {
            return;
        }
        for (Long rodId : rodIds.stream().distinct().collect(Collectors.toList())) {
            BizLiftingRod rod = rodMapper.selectById(rodId);
            if (rod == null || !rod.isValid()) {
                throw new RRException("绑定的杆不存在或已删除，rodId=" + rodId);
            }
            BizLiftingStrategyRod rel = new BizLiftingStrategyRod();
            rel.setStrategyId(strategyId);
            rel.setRodId(rodId);
            relationMapper.insert(rel);
        }
    }

    private LiftingStrategyVO buildVO(BizLiftingStrategy s) {
        LiftingStrategyVO vo = new LiftingStrategyVO();
        vo.setStrategyId(s.getStrategyId());
        vo.setStrategyName(s.getStrategyName());
        vo.setStrategyAction(s.getStrategyAction());
        vo.setStrategyActionDesc(RodActionEnum.descOf(s.getStrategyAction()));
        vo.setStrategyType(s.getStrategyType());
        vo.setStrategyTypeDesc(StrategyTypeEnum.descOf(s.getStrategyType()));
        vo.setStrategyDates(s.getStrategyDates());
        vo.setStrategyRemark(s.getStrategyRemark());
        vo.setStrategyCheckState(s.getStrategyCheckState());
        vo.setStrategyCheckStateDesc(StrategyCheckStateEnum.descOf(s.getStrategyCheckState()));
        vo.setStrategyCreatetime(s.getStrategyCreatetime());
        vo.setStrategyUpdatetime(s.getStrategyUpdatetime());
        vo.setRodIds(new ArrayList<>());
        return vo;
    }

    private void fillDetailAndRods(LiftingStrategyVO vo, Long strategyId) {
        BizLiftingStrategyDetail detail = getDetail(strategyId);
        if (detail != null) {
            vo.setDetailBegin(detail.getDetailBegin());
            vo.setDetailEnd(detail.getDetailEnd());
        }
        List<BizLiftingStrategyRod> rels = relationMapper.selectList(new LambdaQueryWrapper<BizLiftingStrategyRod>()
                .eq(BizLiftingStrategyRod::getStrategyId, strategyId));
        vo.setRodIds(rels.stream().map(BizLiftingStrategyRod::getRodId).collect(Collectors.toList()));
    }
}
