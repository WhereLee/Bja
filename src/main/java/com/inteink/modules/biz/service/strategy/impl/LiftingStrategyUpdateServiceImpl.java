package com.inteink.modules.biz.service.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inteink.modules.biz.annotation.*;
import com.inteink.modules.biz.assembler.StrategyAssembler;
import com.inteink.modules.biz.assembler.StrategyLogAssembler;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.rule.StrategyRuleEngine;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyDetailService;
import com.inteink.modules.biz.service.lifting.BizLiftingStrategyRodService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import com.inteink.modules.biz.service.strategy.AbstractLiftingStrategyService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyUpdateService;

import com.inteink.modules.biz.service.validator.LiftingStrategyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * 升降策略修改服务实现类
 * 修复版：保留现有AOP注解 + 还原完整修改逻辑（校验/明细更新/杆绑定/结构化日志）
 */
@Slf4j
@Service
@RequiredArgsConstructor // 替换@Autowired，兼容依赖注入
// 保留继承父类，复用基础方法+AOP
public class LiftingStrategyUpdateServiceImpl extends AbstractLiftingStrategyService implements LiftingStrategyUpdateService {

    // 还原核心依赖注入
    private final StrategyRuleEngine strategyRuleEngine;
    private final StrategyAssembler strategyAssembler;
    private final BizLiftingStrategyDetailService detailService;
    private final BizLiftingStrategyRodService rodService;
    private final com.inteink.modules.biz.service.impl.BizLiftingStrategyLogServiceImpl strategyLogService;
    private final LiftingStrategyValidator liftingStrategyValidator;
    private final LiftingRodService liftingRodService;

    @Override
    @Transactional(rollbackFor = Exception.class) // 保留事务
    // 保留所有现有AOP注解，一字不改
    @StrategyOperLog(logType = BizLiftingStrategyEnum.LOG_TYPE_UPDATE)
    @StrategyLifeCycleCheck
    @StrategyExceptionHandler
    @StrategyLogRecord
    @StrategyTimeCost
    @StrategyCacheEvict
    public StrategyResponseVO updateStrategyWithDetail(StrategyUpdateDTO updateDTO) {
        log.info("【策略修改】开始处理，策略ID：{}", updateDTO.getStrategyId());

        // ========== 1. 还原：前置校验（核心，避免非法修改） ==========
        // 1.1 查询原策略（复用父类方法 + 精准校验）
        BizLiftingStrategy oldStrategy = getStrategyById(updateDTO.getStrategyId());
        // 校验策略存在且未删除
        liftingStrategyValidator.validateStrategyExistAndNotDeleted(oldStrategy, updateDTO.getStrategyId());
        // 校验更新前置规则（如已审核策略禁止修改）
        strategyRuleEngine.validateUpdatePreRule(oldStrategy);
        // 校验DTO合法性（执行时间/杆ID格式等）
        strategyRuleEngine.validateUpdateDTO(updateDTO);

        // ========== 2. 还原：DTO转实体 + 字段填充 ==========
        BizLiftingStrategy updateEntity = strategyAssembler.convertUpdateDTOToStrategy(updateDTO);
        updateEntity.setStrategyUpdatetime(getCurrentTimestamp()); // 保留现有时间戳
        updateEntity.setStrategyUpdatetime(Instant.now().getEpochSecond()); // 补充双重保障

        // ========== 3. 保留现有：更新主表 ==========
        int update = strategyMapper.updateById(updateEntity);
        if (update <= 0) {
            log.error("【策略修改】主表更新失败，策略ID：{}", updateDTO.getStrategyId());
            throw StrategyBizException.systemError("策略修改失败");
        }

        // ========== 4. 还原：解析明细+杆ID + 明细校验 ==========
        List<BizLiftingStrategyDetail> detailList = strategyAssembler.parseDetailTimeToDetailList(updateDTO.getDetailTime());
        List<Long> rodIds = strategyAssembler.parseRodIdsToLongList(updateDTO.getRodIds());
        log.info("【策略修改】解析明细数量：{}，解析杆ID数量：{}", detailList.size(), rodIds.size());

        // 明细非空校验，避免无效策略
        if (detailList.isEmpty()) {
            throw StrategyBizException.systemError("执行时间格式错误，仅支持HH:mm或HH:mm,HH:mm格式");
        }

        // ========== 5. 还原：更新明细（删旧存新） ==========
        // 删除旧明细
        int oldDetailCount = detailService.count(Wrappers.<BizLiftingStrategyDetail>lambdaQuery()
                .eq(BizLiftingStrategyDetail::getStrategyId, updateDTO.getStrategyId()));
        detailService.remove(Wrappers.<BizLiftingStrategyDetail>lambdaQuery()
                .eq(BizLiftingStrategyDetail::getStrategyId, updateDTO.getStrategyId()));
        log.info("【策略修改】删除旧明细数量：{}", oldDetailCount);

        // 批量保存新明细（500批次提升性能）
        if (!detailList.isEmpty()) {
            detailList.forEach(detail -> detail.setStrategyId(updateDTO.getStrategyId()));
            boolean saveNewDetail = detailService.saveBatch(detailList, 500);
            if (!saveNewDetail) {
                log.error("【策略修改】新明细保存失败，策略ID：{}", updateDTO.getStrategyId());
                throw StrategyBizException.systemError("策略明细保存失败");
            }
            log.info("【策略修改】保存新明细数量：{}", detailList.size());
        }

        // ========== 6. 还原：更新杆绑定（解旧绑新） ==========
        // 解绑旧杆
        int oldRodCount = rodService.getRodIdsByStrategyId(updateDTO.getStrategyId()).size();
        rodService.removeByStrategyId(updateDTO.getStrategyId());
        log.info("【策略修改】解绑旧杆数量：{}", oldRodCount);

        // 绑定新杆
        if (!rodIds.isEmpty()) {
            boolean bindNewRod = rodService.bindStrategyRods(updateDTO.getStrategyId(), rodIds);
            if (!bindNewRod) {
                log.error("【策略修改】杆绑定失败，策略ID：{}，杆ID列表：{}", updateDTO.getStrategyId(), rodIds);
                throw StrategyBizException.systemError("策略-升降杆绑定失败");
            }
            log.info("【策略修改】绑定新杆数量：{}", rodIds.size());
        }

        // ========== 7. 还原：结构化日志记录（容错设计） ==========
        try {
            // 格式化杆信息（9(西二门)、10(北一门)）
            String rodInfo = "";
            List<LiftingRodVO> rodList = liftingRodService.getRodDetailListByIds(rodIds);
            if (!rodList.isEmpty()) {
                rodInfo = strategyAssembler.formatRodInfo(rodList);
            }

            // 格式化执行时段（兼容单/双时间）
            String timePeriod = strategyAssembler.formatTimePeriod(detailList);

            // 拼接结构化日志备注
            String logRemark = StrategyLogAssembler.buildUpdateLogExtRemark(
                    updateDTO,
                    updateEntity,
                    rodInfo,
                    timePeriod
            );

            // 保存日志（替换魔法值为枚举）
            strategyLogService.saveStrategyLog(
                    updateDTO.getStrategyId(),
                    BizLiftingStrategyEnum.LOG_TYPE_UPDATE.getLogTypeCode(),
                    logRemark,
                    updateDTO.getStrategyUpdater()
            );
            log.info("【策略修改】日志记录成功，策略ID：{}", updateDTO.getStrategyId());
        } catch (Exception e) {
            log.warn("【策略修改】日志记录失败，策略ID：{}", updateDTO.getStrategyId(), e);
        }

        // ========== 8. 保留现有：查询新策略 + 组装VO ==========
        BizLiftingStrategy newStrategy = getStrategyById(updateDTO.getStrategyId());
        List<LiftingRodVO> rodList = liftingRodService.getRodDetailListByIds(rodIds);
        String msg = String.format("策略修改成功，明细更新%s条，杆绑定更新%s条", detailList.size(), rodIds.size());
        log.info("【策略修改】处理完成，策略ID：{}，返回提示：{}", updateDTO.getStrategyId(), msg);

        // 组装VO + 补充状态字段（前端友好）
        StrategyResponseVO vo = strategyAssembler.assembleStrategyResponseVOWithRodList(newStrategy, detailList, rodList);
        vo.setStrategyCheckState(newStrategy.getStrategyCheckState());
        vo.setStrategyStatus(newStrategy.getStrategyStatus());

        return vo;
    }
}