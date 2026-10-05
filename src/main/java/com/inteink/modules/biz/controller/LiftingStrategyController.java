package com.inteink.modules.biz.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.form.StrategyForm;
import com.inteink.modules.biz.model.form.StrategyQueryForm;
import com.inteink.modules.biz.model.vo.LiftingStrategyVO;
import com.inteink.modules.biz.service.LiftingStrategyService;
import com.inteink.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 升降策略管理：CRUD + 审核 + 暂停/恢复 + 立即执行 + 审核日志。
 */
@Api(tags = "升降策略管理")
@RestController
@RequestMapping("/biz/liftingstrategy")
@RequiredArgsConstructor
public class LiftingStrategyController extends AbstractController {

    private final LiftingStrategyService liftingStrategyService;

    @ApiOperation("分页查询策略")
    @ApiOperationSupport(order = 1)
    @GetMapping("/page")
    @RequiresPermissions("biz:liftingstrategy:list")
    public Result<PageUtils> page(StrategyQueryForm form) {
        return Result.ok(liftingStrategyService.queryPage(form));
    }

    @ApiOperation("策略详情")
    @ApiOperationSupport(order = 2)
    @GetMapping("/detail/{strategyId}")
    @RequiresPermissions("biz:liftingstrategy:info")
    public Result<LiftingStrategyVO> detail(@PathVariable("strategyId") Long strategyId) {
        return Result.ok(liftingStrategyService.detail(strategyId));
    }

    @ApiOperation("新增策略（待审核）")
    @ApiOperationSupport(order = 3)
    @PostMapping("/save")
    @RequiresPermissions("biz:liftingstrategy:save")
    public Result<Long> save(@RequestBody StrategyForm form) {
        return Result.ok(liftingStrategyService.saveStrategy(form, getUserId()), "新增成功，待审核");
    }

    @ApiOperation("修改策略")
    @ApiOperationSupport(order = 4)
    @PostMapping("/update")
    @RequiresPermissions("biz:liftingstrategy:update")
    public Result update(@RequestBody StrategyForm form) {
        liftingStrategyService.updateStrategy(form, getUserId());
        return Result.ok();
    }

    @ApiOperation("审核（通过则生成定时任务）")
    @ApiOperationSupport(order = 5)
    @PostMapping("/audit/{strategyId}")
    @RequiresPermissions("biz:liftingstrategy:audit")
    public Result audit(@PathVariable("strategyId") Long strategyId,
                        @RequestParam("pass") boolean pass,
                        @RequestParam(value = "remark", required = false) String remark) {
        liftingStrategyService.audit(strategyId, pass, remark, getUserId());
        return Result.ok(null, pass ? "审核通过" : "已驳回");
    }

    @ApiOperation("删除策略（解绑+清定时+逻辑删）")
    @ApiOperationSupport(order = 6)
    @PostMapping("/remove/{strategyId}")
    @RequiresPermissions("biz:liftingstrategy:delete")
    public Result remove(@PathVariable("strategyId") Long strategyId) {
        liftingStrategyService.removeStrategy(strategyId);
        return Result.ok();
    }

    @ApiOperation("暂停策略定时")
    @ApiOperationSupport(order = 7)
    @PostMapping("/pause/{strategyId}")
    @RequiresPermissions("biz:liftingstrategy:pause")
    public Result pause(@PathVariable("strategyId") Long strategyId) {
        liftingStrategyService.pause(strategyId);
        return Result.ok(null, "已暂停");
    }

    @ApiOperation("恢复策略定时")
    @ApiOperationSupport(order = 8)
    @PostMapping("/resume/{strategyId}")
    @RequiresPermissions("biz:liftingstrategy:resume")
    public Result resume(@PathVariable("strategyId") Long strategyId) {
        liftingStrategyService.resume(strategyId);
        return Result.ok(null, "已恢复");
    }

    @ApiOperation("立即执行策略")
    @ApiOperationSupport(order = 9)
    @PostMapping("/execute/{strategyId}")
    @RequiresPermissions("biz:liftingstrategy:update")
    public Result execute(@PathVariable("strategyId") Long strategyId) {
        int n = liftingStrategyService.executeNow(strategyId);
        return Result.ok(n, "已驱动 " + n + " 根杆");
    }

    @ApiOperation("策略审核日志")
    @ApiOperationSupport(order = 10)
    @GetMapping("/log/{strategyId}")
    @RequiresPermissions("biz:liftingstrategy:info")
    public Result<List<BizLiftingStrategyLog>> log(@PathVariable("strategyId") Long strategyId) {
        return Result.ok(liftingStrategyService.auditLogs(strategyId));
    }
}
