package com.inteink.modules.biz.controller;

import com.inteink.common.utils.Result;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.dto.StrategyQueryDTO;
import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.service.lifting.LiftingStrategyService;
//import com.inteink.modules.biz.service.strategy.LiftingStrategyExecuteService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyExecuteService;
import com.inteink.modules.biz.service.strategy.LiftingStrategyPauseResumeService;
import com.inteink.modules.biz.model.vo.StrategyExecuteResultVO;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 升降策略控制器（极致轻量化）
 * 核心职责：
 * 1. 接收前端请求参数（DTO）；
 * 2. 调用Service层处理业务逻辑；
 * 3. 封装统一返回结果（Result）；
 * 无任何业务逻辑、参数校验（交给DTO注解+Service规则引擎）。
 */
@Slf4j
@RestController
@RequestMapping("/biz/lifting-strategy")
@RequiredArgsConstructor
@Api(tags = "升降策略管理")
public class LiftingStrategyController {

    private final LiftingStrategyService liftingStrategyService;
    private final LiftingStrategyPauseResumeService pauseResumeService;
    private final LiftingStrategyExecuteService strategyExecuteService;

    @ApiOperation("新增升降策略")
    @PostMapping("/save")
    public Result<StrategyResponseVO> saveStrategy(
            @ApiParam("新增策略参数") @Validated @RequestBody StrategySaveDTO saveDTO
    ) {
        log.info("新增升降策略请求：{}", saveDTO);
        StrategyResponseVO responseVO = liftingStrategyService.saveStrategyWithDetail(saveDTO);
        return Result.ok(responseVO, "新增策略成功");
    }

    @ApiOperation("修改升降策略")
    @PostMapping("/update")
    public Result<StrategyResponseVO> updateStrategy(
            @ApiParam("修改策略参数") @Validated @RequestBody StrategyUpdateDTO updateDTO
    ) {
        log.info("修改升降策略请求：{}", updateDTO);
        StrategyResponseVO responseVO = liftingStrategyService.updateStrategyWithDetail(updateDTO);
        return Result.ok(responseVO, "修改策略成功");
    }

    @ApiOperation("策略审核（通过/驳回）")
    @PostMapping("/audit")
    public Result<StrategyResponseVO> auditStrategy(
            @ApiParam("审核策略参数") @Validated @RequestBody StrategyAuditDTO auditDTO
    ) {
        log.info("审核升降策略请求：{}", auditDTO);
        Result<StrategyResponseVO> serviceResult = liftingStrategyService.auditStrategy(auditDTO);
        return serviceResult;
    }

    @ApiOperation("分页查询策略列表")
    @PostMapping("/page")
    public Result<PageUtils> queryPage(
            @ApiParam("分页查询参数") @Validated @RequestBody StrategyQueryDTO queryDTO
    ) {
        log.info("分页查询升降策略请求：{}", queryDTO);
        PageUtils pageUtils = liftingStrategyService.queryPage(queryDTO);
        return Result.ok(pageUtils);
    }

    @ApiOperation("查询策略详情")
    @GetMapping("/detail/{strategyId}")
    public Result<StrategyResponseVO> getDetail(
            @ApiParam("策略ID") @PathVariable Long strategyId
    ) {
        log.info("查询升降策略详情请求：strategyId={}", strategyId);
        StrategyResponseVO responseVO = liftingStrategyService.getStrategyDetailWithRods(strategyId);
        return Result.ok(responseVO);
    }

    @ApiOperation("删除策略（逻辑删除+解绑杆+清定时任务）")
    @PostMapping("/remove/{strategyId}")
    public Result<StrategyResponseVO> removeStrategy(
            @ApiParam("策略ID") @PathVariable Long strategyId
    ) {
        log.info("删除升降策略请求：strategyId={}", strategyId);
        StrategyResponseVO responseVO = liftingStrategyService.removeStrategyWithDetail(strategyId);
        return Result.ok(responseVO, "删除策略成功");
    }

    @ApiOperation("查询策略操作日志")
    @GetMapping("/log/{strategyId}")
    public Result<List<BizLiftingStrategyLog>> getLog(
            @ApiParam("策略ID") @PathVariable Long strategyId
    ) {
        log.info("查询升降策略日志请求：strategyId={}", strategyId);
        List<BizLiftingStrategyLog> logList = liftingStrategyService.getStrategyLog(strategyId);
        return Result.ok(logList);
    }

    @ApiOperation("立即执行策略（手动触发升降杆操作）")
    @GetMapping("/execute/{strategyId}")
    public Result<StrategyExecuteResultVO> executeStrategy(
            @ApiParam("策略ID") @PathVariable Long strategyId,
            @ApiParam("执行动作：1-升杆/2-降杆（为空则使用策略配置的动作）") @RequestParam(required = false) Integer action
    ) {
        log.info("立即执行升降策略请求：strategyId={}, action={}", strategyId, action);
        StrategyExecuteResultVO executeResult = strategyExecuteService.execute(strategyId, action);
        String msg = executeResult.getSuccessCount() > 0
                ? "策略执行成功"
                : "策略执行无有效杆操作（失败/跳过）";
        return Result.ok(executeResult, msg);
    }

    @ApiOperation("暂停策略")
    @PostMapping("/pause/{strategyId}")
    public Result<StrategyResponseVO> pauseStrategy(
            @ApiParam("策略ID") @PathVariable Long strategyId,
            @ApiParam("操作人ID") @RequestParam Long operator
    ) {
        log.info("暂停升降策略请求：strategyId={}, operator={}", strategyId, operator);
        StrategyResponseVO responseVO = pauseResumeService.pauseStrategy(strategyId, operator);
        return Result.ok(responseVO, "策略暂停成功");
    }

    @ApiOperation("恢复策略")
    @PostMapping("/resume/{strategyId}")
    public Result<StrategyResponseVO> resumeStrategy(
            @ApiParam("策略ID") @PathVariable Long strategyId,
            @ApiParam("操作人ID") @RequestParam Long operator // 改为表格形式的@RequestParam
    ) {
        log.info("恢复升降策略请求：strategyId={}, operator={}", strategyId, operator);
        StrategyResponseVO responseVO = pauseResumeService.resumeStrategy(strategyId, operator);
        return Result.ok(responseVO, "策略恢复成功");
    }
}