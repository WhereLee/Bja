package com.inteink.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.assembler.StrategyAssembler;
import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.dto.StrategyQueryDTO;
import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.form.LiftingStrategyForm;
import com.inteink.modules.biz.mapper.BizLiftingStrategyMapper;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyAuditService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyRemoveService;
import com.inteink.modules.biz.service.strategy.LiftingStrategySaveService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyUpdateService;
import com.inteink.modules.biz.service.validator.LiftingStrategyValidator;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 升降策略业务门面类（仅做转发，无业务逻辑）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiftingStrategyServiceImpl extends ServiceImpl<BizLiftingStrategyMapper, BizLiftingStrategy> implements LiftingStrategyService {

    // 注入各动作服务类
    private final LiftingStrategySaveService saveService;
    private final LiftingStrategyUpdateService updateService;
    private final LiftingStrategyAuditService auditService;
    private final LiftingStrategyRemoveService removeService;
    private final StrategyAssembler strategyAssembler;
    private final BizLiftingStrategyDetailService detailService;
    private final BizLiftingStrategyRodService rodService;
    private final LiftingStrategyValidator liftingStrategyValidator;

    // ========== 转发核心业务方法 ==========
    @Override
    public StrategyResponseVO saveStrategyWithDetail(StrategySaveDTO saveDTO) {
        return saveService.saveStrategyWithDetail(saveDTO);
    }

    @Override
    public StrategyResponseVO updateStrategyWithDetail(StrategyUpdateDTO updateDTO) {
        return updateService.updateStrategyWithDetail(updateDTO);
    }

//    @Override
//    public StrategyResponseVO auditStrategy(StrategyAuditDTO auditDTO) {
//        return auditService.auditStrategy(auditDTO);
//    }

    @Override
    public Result<StrategyResponseVO> auditStrategy(StrategyAuditDTO auditDTO) {
        // 直接返回Service的Result，无需转换，类型完全匹配
        return auditService.auditStrategy(auditDTO);
    }

    @Override
    public StrategyResponseVO removeStrategyWithDetail(Long strategyId) {
        return removeService.removeStrategyWithDetail(strategyId);
    }

    // ========== 保留轻量查询方法 ==========
    @Override
    public PageUtils queryPage(StrategyQueryDTO queryDTO) {
        LiftingStrategyForm form = new LiftingStrategyForm();
        BeanUtils.copyProperties(queryDTO, form);

        LambdaQueryWrapper<BizLiftingStrategy> wrapper = new LambdaQueryWrapper<BizLiftingStrategy>()
                .eq(BizLiftingStrategy::getStrategyStatus, 0L)
                .like(StringUtils.isNotBlank(form.getStrategyName()), BizLiftingStrategy::getStrategyName, form.getStrategyName())
                .eq(Objects.nonNull(form.getStrategyAction()), BizLiftingStrategy::getStrategyAction, form.getStrategyAction())
                .eq(Objects.nonNull(form.getStrategyCheckState()), BizLiftingStrategy::getStrategyCheckState, form.getStrategyCheckState());

        Page<BizLiftingStrategy> page = this.page(new Page<>(form.getPageNum(), form.getPageSize()), wrapper);
        return new PageUtils(page);
    }

    @Override
    public StrategyResponseVO getStrategyDetailWithRods(Long strategyId) {
        BizLiftingStrategy strategy = this.getById(strategyId);
        liftingStrategyValidator.validateStrategyExistAndNotDeleted(strategy, strategyId);

        List<BizLiftingStrategyDetail> detailList = detailService.list(Wrappers.<BizLiftingStrategyDetail>lambdaQuery()
                .eq(BizLiftingStrategyDetail::getStrategyId, strategyId));

        List<Long> rodIds = rodService.getRodIdsByStrategyId(strategyId);

        return strategyAssembler.assembleStrategyResponseVO(strategy, detailList, rodIds);
    }

    @Override
    public List<BizLiftingStrategyLog> getStrategyLog(Long strategyId) {
        return null;
    }

    // 彻底删除错误的静态getBaseMapper方法（核心修正）
}