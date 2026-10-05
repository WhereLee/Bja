package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.entity.BizLiftingRodLog;
import com.inteink.modules.biz.model.eums.RodStatusEnum;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.mapper.BizLiftingRodLogMapper;
import com.inteink.modules.biz.mapper.BizLiftingRodMapper;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LiftingRodServiceImpl extends ServiceImpl<BizLiftingRodMapper, BizLiftingRod> implements LiftingRodService {

    @Resource
    private BizLiftingRodLogMapper liftingRodLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void liftRod(Long rodId, Integer action, Long operator) {
        BizLiftingRod rod = this.getById(rodId);
        if (rod == null) {
            System.out.println("【错误】升降杆ID：" + rodId + " 不存在！");
            return;
        }

        BizLiftingRodLog.LogActionEnum actionEnum = BizLiftingRodLog.LogActionEnum.getByCode(action);
        if (actionEnum == null) {
            System.out.println("【错误】动作不合法！只能传1（升）或2（降）");
            return;
        }

        rod.setRodState(action);
        rod.setRodUpdatetime(System.currentTimeMillis() / 1000);
        this.updateById(rod);

        BizLiftingRodLog log = new BizLiftingRodLog();
        log.setRodId(rodId);
        log.setLogType(BizLiftingRodLog.LogTypeEnum.MANUAL.getCode());
        log.setLogAction(action);
        log.setStrategyId(null);
        log.setRemark("手动" + actionEnum.getDesc() + "升降杆");
        log.setLogResult(BizLiftingRodLog.LogResultEnum.SUCCESS.getCode());
        log.setLogOperateTime(System.currentTimeMillis() / 1000);
        liftingRodLogMapper.insert(log);
    }

    @Override
    public void liftRod(Long rodId, Integer action) {
        this.liftRod(rodId, action, 0L);
    }

    @Override
    public PageUtils queryLogPage(LiftingRodForm form, Long userId) {
        LambdaQueryWrapper<BizLiftingRodLog> wrapper = new LambdaQueryWrapper<>();
        if (form.getRodId() != null) {
            wrapper.eq(BizLiftingRodLog::getRodId, form.getRodId());
        }
        if (form.getLogType() != null) {
            wrapper.eq(BizLiftingRodLog::getLogType, form.getLogType());
        }
        if (form.getLogAction() != null) {
            wrapper.eq(BizLiftingRodLog::getLogAction, form.getLogAction());
        }

        IPage<BizLiftingRodLog> page = liftingRodLogMapper.selectPage(new Page<>(form.getPageNum(), form.getPageSize()), wrapper);
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryRodPage(LiftingRodForm form) {
        IPage<BizLiftingRod> page = new Page<>(form.getPageNum(), form.getPageSize());
        page = baseMapper.queryRodPage(page, form);
        return new PageUtils(page);
    }

    @Override
    public List<LiftingRodVO> getRodDetailListByIds(List<Long> rodIds) {
        if (rodIds == null || rodIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<BizLiftingRod> rodEntityList = this.listByIds(rodIds);
        List<LiftingRodVO> rodVOList = new ArrayList<>();
        for (BizLiftingRod rodEntity : rodEntityList) {
            LiftingRodVO rodVO = new LiftingRodVO();
            BeanUtils.copyProperties(rodEntity, rodVO);

            Boolean offlineBool = rodEntity.getRodOffline();
            Integer offlineCode = offlineBool == null ? 0 : (offlineBool ? 1 : 0);
            rodVO.setRodOffline(offlineCode);
            String offlineDesc = offlineCode == 1 ? BizLiftingRod.RodOfflineEnum.ONLINE.getDesc() : BizLiftingRod.RodOfflineEnum.OFFLINE.getDesc();
            rodVO.setRodOfflineDesc(offlineDesc);

            Long statusLong = rodEntity.getRodStatus();
            Integer statusCode = statusLong == null ? 1 : statusLong.intValue();
            rodVO.setRodStatus(statusCode);
            rodVO.setRodStatusDesc(RodStatusEnum.getDescByCode(statusCode));
            rodVOList.add(rodVO);
        }
        return rodVOList;
    }

    @Override
    public List<LiftingRodVO> getValidRodListByStrategyId(Long strategyId) {
        List<BizLiftingRod> rodList = this.baseMapper.selectValidRodByStrategyId(strategyId);
        List<LiftingRodVO> voList = new ArrayList<>();
        for (BizLiftingRod rod : rodList) {
            LiftingRodVO vo = new LiftingRodVO();
            BeanUtils.copyProperties(rod, vo);

            Boolean offlineBool = rod.getRodOffline();
            Integer offlineCode = offlineBool == null ? 0 : (offlineBool ? 1 : 0);
            vo.setRodOffline(offlineCode);
            String offlineDesc = offlineCode == 1 ? BizLiftingRod.RodOfflineEnum.ONLINE.getDesc() : BizLiftingRod.RodOfflineEnum.OFFLINE.getDesc();
            vo.setRodOfflineDesc(offlineDesc);

            Long statusLong = rod.getRodStatus();
            Integer statusCode = statusLong == null ? 0 : statusLong.intValue();
            vo.setRodStatus(statusCode);
            vo.setRodStatusDesc(RodStatusEnum.getDescByCode(statusCode));
            voList.add(vo);
        }
        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean controlRod(Long rodId, Integer action) {
        try {
            BizLiftingRod rod = this.getById(rodId);
            if (rod == null) {
                return false;
            }

            BizLiftingRodLog.LogActionEnum actionEnum = BizLiftingRodLog.LogActionEnum.getByCode(action);
            if (actionEnum == null) {
                return false;
            }

            rod.setRodState(action);
            rod.setRodUpdatetime(System.currentTimeMillis() / 1000);
            this.updateById(rod);

            BizLiftingRodLog log = new BizLiftingRodLog();
            log.setRodId(rodId);
            log.setLogType(BizLiftingRodLog.LogTypeEnum.AUTO.getCode());
            log.setLogAction(action);
            log.setStrategyId(null);
            log.setRemark("策略自动" + actionEnum.getDesc() + "升降杆");
            log.setLogResult(BizLiftingRodLog.LogResultEnum.SUCCESS.getCode());
            log.setLogOperateTime(System.currentTimeMillis() / 1000);
            liftingRodLogMapper.insert(log);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}