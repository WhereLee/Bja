package com.inteink.modules.biz.assembler;

import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 升降策略对象转换装配器（DTO→实体、实体→返回VO）,该类是 DTO 与实体、返回 VO 组装的核心
 */
@Component
public class StrategyAssembler {

    /**
     * 生成带秒级时间戳的唯一策略名
     * @param coreName 核心策略名（用户输入的原始名称）
     * @return 核心名_秒级时间戳（如：每天执行_1717380462）
     */
    public String generateUniqueStrategyName(String coreName) {
        // 1. 处理空名称：默认给个基础名
        if (StringUtils.isBlank(coreName)) {
            coreName = "默认策略";
        }
        // 2. 获取当前秒级时间戳（和表里的createtime/updatetime格式一致）
        long timestamp = Instant.now().getEpochSecond();
        // 3. 拼接：核心名 + 下划线 + 时间戳
        return coreName.trim() + "_" + timestamp;
    }

    /**
     * 新增DTO转换为策略主表实体
     */
    public BizLiftingStrategy convertSaveDTOToStrategy(StrategySaveDTO dto) {
        BizLiftingStrategy strategy = new BizLiftingStrategy();
        strategy.setStrategyName(generateUniqueStrategyName(dto.getStrategyName()));
        strategy.setStrategyAction(dto.getStrategyAction());
        strategy.setStrategyType(dto.getStrategyType());
        strategy.setStrategyDates(dto.getStrategyDates());
        strategy.setStrategyRemark(StringUtils.isNotBlank(dto.getStrategyRemark()) ? dto.getStrategyRemark() : "");
        strategy.setStrategyCreator(dto.getStrategyCreator());
        // 新增默认状态：0-待审核
        strategy.setStrategyCheckState(0);
        return strategy;
    }

    /**
     * 修改DTO转换为策略主表实体（仅赋值有值的字段）
     */
    public BizLiftingStrategy convertUpdateDTOToStrategy(StrategyUpdateDTO dto) {
        BizLiftingStrategy strategy = new BizLiftingStrategy();
        strategy.setStrategyId(dto.getStrategyId());
        if (StringUtils.isNotBlank(dto.getStrategyName())) {
            strategy.setStrategyName(generateUniqueStrategyName(dto.getStrategyName()));
        }
        if (dto.getStrategyAction() != null) {
            strategy.setStrategyAction(dto.getStrategyAction());
        }
        if (dto.getStrategyType() != null) {
            strategy.setStrategyType(dto.getStrategyType());
        }
        if (StringUtils.isNotBlank(dto.getStrategyDates())) {
            strategy.setStrategyDates(dto.getStrategyDates());
        }
        if (dto.getStrategyRemark() != null) {
            strategy.setStrategyRemark(dto.getStrategyRemark());
        }
        // 修改后重置审核状态为待审核
        strategy.setStrategyCheckState(0);
        // 对齐新增逻辑：修改后默认保持有效状态（0）
        strategy.setStrategyStatus(0L);
        return strategy;
    }

    /**
     * 解析执行时间为明细列表（兼容单/双时间）
     * 单时间：detailTime=HH:mm → begin=HH:mm，end=null
     * 双时间：detailTime=HH:mm,HH:mm → begin=HH:mm，end=HH:mm
     */
    public List<BizLiftingStrategyDetail> parseDetailTimeToDetailList(String detailTime) {
        List<BizLiftingStrategyDetail> detailList = new ArrayList<>();
        if (StringUtils.isBlank(detailTime)) {
            return detailList;
        }

        BizLiftingStrategyDetail detail = new BizLiftingStrategyDetail();
        // 拆分时间：兼容单/双时间
        String[] timeArr = detailTime.split(",");
        if (timeArr.length == 1) {
            // 单时间场景：begin赋值，end为null
            String singleTime = timeArr[0].trim();
            detail.setDetailBegin(singleTime);
            detail.setDetailEnd(null);
        } else if (timeArr.length == 2) {
            // 双时间场景：拆分begin/end
            detail.setDetailBegin(timeArr[0].trim());
            detail.setDetailEnd(timeArr[1].trim());
        } else {
            // 格式错误（超过2个时间）：返回空列表
            return detailList;
        }

        // 校验时间格式（仅双时间时校验end，单时间仅校验begin）
        boolean isFormatValid = (detail.getDetailEnd() == null)
                ? detail.getDetailBegin().matches("^([01]\\d|2[0-3]):[0-5]\\d$")
                : detail.checkTimeFormat();

        if (isFormatValid) {
            detailList.add(detail);
        }
        return detailList;
    }

    /**
     * 解析杆ID字符串为Long列表
     */
    public List<Long> parseRodIdsToLongList(String rodIds) {
        List<Long> rodIdList = new ArrayList<>();
        if (StringUtils.isBlank(rodIds)) {
            return rodIdList;
        }
        String[] rodIdArr = rodIds.split(",");
        for (String rodIdStr : rodIdArr) {
            try {
                rodIdList.add(Long.parseLong(rodIdStr.trim()));
            } catch (NumberFormatException e) {
                // 非法杆ID忽略，由RuleEngine提前校验
                continue;
            }
        }
        return rodIdList;
    }

    /**
     * 【优化】策略实体+明细+杆ID组装为返回VO（对齐新增的完整VO逻辑）
     */
    public StrategyResponseVO assembleStrategyResponseVO(BizLiftingStrategy strategy,
                                                         List<BizLiftingStrategyDetail> detailList,
                                                         List<Long> rodIds) {
        // 空值兜底：避免传入null导致NPE
        detailList = detailList == null ? new ArrayList<>() : detailList;
        rodIds = rodIds == null ? new ArrayList<>() : rodIds;

        StrategyResponseVO responseVO = StrategyResponseVO.buildBaseVO(strategy);
        // 1. 组装明细列表
        responseVO.setDetailList(detailList);

        // 2. 补充审核状态描述（对齐新增的枚举兜底）
        Integer checkState = strategy.getStrategyCheckState();
        String checkStateDesc = BizLiftingStrategyEnum.getCheckStateDesc(checkState);
        responseVO.setStrategyCheckStateDesc(checkStateDesc);

        // 3. 补充策略状态描述（新增逻辑有，修改侧补充）
        Long status = strategy.getStrategyStatus();
        String statusDesc = BizLiftingStrategyEnum.getStrategyStatusDesc(status);
        responseVO.setStrategyStatusDesc(statusDesc);

        // 4. 对齐新增的状态字段赋值
        responseVO.setStrategyCheckState(strategy.getStrategyCheckState());
        responseVO.setStrategyStatus(strategy.getStrategyStatus());

        // 兜底修正：状态描述+空列表
        fixStrategyResponseVO(responseVO);

        return responseVO;
    }

    /**
     * 策略实体快速组装为返回VO（仅基础信息）（适配新VO结构）
     */
    public StrategyResponseVO assembleSimpleResponseVO(BizLiftingStrategy strategy, String msg) {
        StrategyResponseVO responseVO = StrategyResponseVO.buildBaseVO(strategy);
        // 补充审核状态描述（适配新VO）
        Integer checkState = strategy.getStrategyCheckState();
        String checkStateDesc = BizLiftingStrategyEnum.getCheckStateDesc(checkState);
        responseVO.setStrategyCheckStateDesc(checkStateDesc);

        // 补充策略状态描述（新增逻辑有，修改侧补充）
        Long status = strategy.getStrategyStatus();
        String statusDesc = BizLiftingStrategyEnum.getStrategyStatusDesc(status);
        responseVO.setStrategyStatusDesc(statusDesc);

        // 兜底修正：状态描述+空列表
        fixStrategyResponseVO(responseVO);

        return responseVO;
    }

    /**
     * 【新增核心】策略实体+明细+杆完整列表组装为返回VO（解决泛型擦除冲突，重命名方法）
     */
    public StrategyResponseVO assembleStrategyResponseVOWithRodList(BizLiftingStrategy strategy,
                                                                    List<BizLiftingStrategyDetail> detailList,
                                                                    List<LiftingRodVO> rodList) {
        StrategyResponseVO responseVO = StrategyResponseVO.buildBaseVO(strategy);
        // 1. 组装明细列表
        responseVO.setDetailList(detailList == null ? new ArrayList<BizLiftingStrategyDetail>() : detailList);
        // 2. 组装杆完整列表
        responseVO.setRodList(rodList == null ? new ArrayList<LiftingRodVO>() : rodList);
        // 3. 补充审核状态描述
        Integer checkState = strategy.getStrategyCheckState();
        String checkStateDesc = BizLiftingStrategyEnum.getCheckStateDesc(checkState);
        responseVO.setStrategyCheckStateDesc(checkStateDesc);
        // 4. 补充策略状态描述
        Long status = strategy.getStrategyStatus();
        String statusDesc = BizLiftingStrategyEnum.getStrategyStatusDesc(status);
        responseVO.setStrategyStatusDesc(statusDesc);

        // 对齐新增：显式赋值状态字段
        responseVO.setStrategyCheckState(strategy.getStrategyCheckState());
        responseVO.setStrategyStatus(strategy.getStrategyStatus());

        // 兜底修正：状态描述+空列表
        fixStrategyResponseVO(responseVO);

        return responseVO;
    }

    /**
     * 兜底修正返回VO：status=0设为“有效”，空列表赋值
     */
    private void fixStrategyResponseVO(StrategyResponseVO responseVO) {
        // 1. 修正strategyStatusDesc：0代表有效（对齐新增逻辑）
        if (responseVO.getStrategyStatus() != null && responseVO.getStrategyStatus() == 0) {
            responseVO.setStrategyStatusDesc("有效");
        }
        // 2. 确保rodList返回空列表而非null
        if (responseVO.getRodList() == null) {
            responseVO.setRodList(new ArrayList<>());
        }
        // 3. 确保detailList返回空列表而非null
        if (responseVO.getDetailList() == null) {
            responseVO.setDetailList(new ArrayList<>());
        }
        // 4. 兜底审核状态描述（避免null）
        if (StringUtils.isBlank(responseVO.getStrategyCheckStateDesc())) {
            responseVO.setStrategyCheckStateDesc("未知");
        }
        // 5. 兜底策略状态描述（避免null）
        if (StringUtils.isBlank(responseVO.getStrategyStatusDesc())) {
            responseVO.setStrategyStatusDesc("未知");
        }
    }

    /**
     * 【新增】格式化执行时段（对齐新增日志的时间展示逻辑）
     * 供修改侧日志拼接复用
     */
    public String formatTimePeriod(List<BizLiftingStrategyDetail> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return "无";
        }
        BizLiftingStrategyDetail firstDetail = detailList.get(0);
        if (firstDetail.getDetailEnd() == null || firstDetail.getDetailEnd().isEmpty()) {
            // 单时间：仅显示begin
            return firstDetail.getDetailBegin();
        } else {
            // 双时间：显示begin-end
            return firstDetail.getDetailBegin() + "-" + firstDetail.getDetailEnd();
        }
    }

    /**
     * 【新增】格式化杆信息（对齐新增日志的杆展示逻辑：9(西二门)、10(北一门)）
     * 供修改侧日志拼接复用
     */
    public String formatRodInfo(List<LiftingRodVO> rodList) {
        if (rodList == null || rodList.isEmpty()) {
            return "无";
        }
        StringBuilder rodInfo = new StringBuilder();
        for (int i = 0; i < rodList.size(); i++) {
            LiftingRodVO rod = rodList.get(i);
            rodInfo.append(rod.getRodId()).append("(").append(rod.getRodName()).append(")");
            if (i < rodList.size() - 1) {
                rodInfo.append("、");
            }
        }
        return rodInfo.toString();
    }

}