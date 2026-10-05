package com.inteink.modules.biz.service.strategy.impl;

import com.inteink.modules.biz.annotation.*;
import com.inteink.modules.biz.assembler.StrategyAssembler;
import com.inteink.modules.biz.assembler.StrategyLogAssembler;
import com.inteink.modules.biz.exception.StrategyBizException;
import com.inteink.modules.biz.model.dto.StrategySaveDTO;
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
import com.inteink.modules.biz.service.strategy.LiftingStrategySaveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

/**
 * 升降策略新增服务实现类
 * 修复版：保留现有AOP注解 + 还原完整业务逻辑（规则校验/明细保存/杆绑定/结构化日志）
 */
@Slf4j
@Service
@RequiredArgsConstructor // 替换@Autowired，保留依赖注入方式
// 保留继承父类，兼容现有AOP/基础方法
public class LiftingStrategySaveServiceImpl extends AbstractLiftingStrategyService implements LiftingStrategySaveService {

    // 保留现有AOP注解，完全不动
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    // 还原之前的完整依赖注入
    private final StrategyRuleEngine strategyRuleEngine;
    private final StrategyAssembler strategyAssembler;
    private final BizLiftingStrategyDetailService detailService;
    private final BizLiftingStrategyRodService rodService;
    private final com.inteink.modules.biz.service.impl.BizLiftingStrategyLogServiceImpl strategyLogService;
    private final LiftingRodService liftingRodService;

    @Override
    @Transactional(rollbackFor = Exception.class) // 保留事务
    // 保留所有现有AOP注解，一字不改
    @StrategyOperLog(logType = BizLiftingStrategyEnum.LOG_TYPE_ADD)
    @StrategyExceptionHandler
    @StrategyLogRecord
    @StrategyTimeCost
    @StrategyCacheEvict
    public StrategyResponseVO saveStrategyWithDetail(StrategySaveDTO saveDTO) {
        // ========== 1. 还原：入参规则校验（核心） ==========
        strategyRuleEngine.validateSaveDTO(saveDTO);

        // ========== 2. 还原：DTO转实体 + 字段填充 ==========
        BizLiftingStrategy strategy = strategyAssembler.convertSaveDTOToStrategy(saveDTO);
        // 保留父类方法 + 补充手动填充（双重保障）
        fillBaseCreateFields(strategy, saveDTO.getStrategyCreator());
        long now = Instant.now().getEpochSecond();
        strategy.setStrategyCreatetime(now);
        strategy.setStrategyUpdatetime(now);
        strategy.setStrategyStatus(0L); // 0=有效
        strategy.setStrategyCheckState(0); // 0=待审核

        // ========== 3. 还原：解析明细+杆ID + 明细校验 ==========
        List<BizLiftingStrategyDetail> detailList = strategyAssembler.parseDetailTimeToDetailList(saveDTO.getDetailTime());
        List<Long> rodIds = strategyAssembler.parseRodIdsToLongList(saveDTO.getRodIds());

        // 补充：明细非空校验（避免无有效执行时段）
        if (detailList.isEmpty()) {
            throw StrategyBizException.systemError("执行时间格式错误，仅支持HH:mm或HH:mm,HH:mm格式");
        }

        // ========== 4. 保留现有：保存主表 ==========
        int save = strategyMapper.insert(strategy);
        if (save <= 0) {
            throw StrategyBizException.systemError("策略新增失败");
        }

        // ========== 5. 还原：批量保存明细 ==========
        if (!detailList.isEmpty()) {
            detailList.forEach(detail -> detail.setStrategyId(strategy.getStrategyId()));
            boolean saveDetail = detailService.saveBatch(detailList, 500);
            if (!saveDetail) {
                throw StrategyBizException.systemError("策略明细保存失败");
            }
        }

        // ========== 6. 还原：绑定升降杆（核心关联逻辑） ==========
        if (!rodIds.isEmpty()) {
            boolean bindRod = rodService.bindStrategyRods(strategy.getStrategyId(), rodIds);
            if (!bindRod) {
                throw StrategyBizException.systemError("策略-升降杆绑定失败");
            }
        }

        // ========== 7. 还原：结构化日志记录（容错设计） ==========
        try {
            // 7.1 复用核心日志字段
            String coreLog = StrategyLogAssembler.buildStrategyCoreLog(strategy);

            // 7.2 处理多杆信息（拼接：9(西二门)、10(北一门)）
            String rodInfo = "";
            List<LiftingRodVO> rodList = liftingRodService.getRodDetailListByIds(rodIds);
            if (!rodList.isEmpty()) {
                StringJoiner rodJoiner = new StringJoiner("、");
                for (LiftingRodVO rod : rodList) {
                    rodJoiner.add(rod.getRodId() + "(" + rod.getRodName() + ")");
                }
                rodInfo = rodJoiner.toString();
            }

            // 7.3 处理执行时段（兼容单/双时间）
            String timePeriod = "";
            BizLiftingStrategyDetail firstDetail = detailList.get(0);
            if (firstDetail.getDetailEnd() == null || firstDetail.getDetailEnd().isEmpty()) {
                timePeriod = firstDetail.getDetailBegin();
            } else {
                timePeriod = firstDetail.getDetailBegin() + "-" + firstDetail.getDetailEnd();
            }

            // 7.4 拼接日志备注
            String logRemark = String.format(
                    "新增策略：%s,strategyAction=%s,strategyRemark=%s，绑定杆ID=%s，执行时段=%s，操作人=%s，送往审核（状态：待审核/有效）。",
                    coreLog,
                    BizLiftingStrategyEnum.getActionDesc(strategy.getStrategyAction()),
                    strategy.getStrategyRemark(),
                    rodInfo,
                    timePeriod,
                    saveDTO.getStrategyCreator()
            );

            // 7.5 保存日志（替换魔法值为枚举）
            strategyLogService.saveStrategyLog(
                    strategy.getStrategyId(),
                    BizLiftingStrategyEnum.LOG_TYPE_ADD.getLogTypeCode(),
                    logRemark,
                    saveDTO.getStrategyCreator()
            );
        } catch (Exception e) {
            log.warn("日志保存失败，策略ID：{}", strategy.getStrategyId(), e);
        }

        // ========== 8. 保留现有：查询杆详情 + 组装返回VO ==========
        List<LiftingRodVO> rodList = liftingRodService.getRodDetailListByIds(rodIds);
        StrategyResponseVO vo = strategyAssembler.assembleStrategyResponseVOWithRodList(strategy, detailList, rodList);

        // 补充：还原VO字段（审核状态/策略状态）
        vo.setStrategyCheckState(strategy.getStrategyCheckState());
        vo.setStrategyStatus(strategy.getStrategyStatus());

        return vo;
    }
}