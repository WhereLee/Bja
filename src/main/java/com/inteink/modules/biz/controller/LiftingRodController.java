package com.inteink.modules.biz.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.enums.RodActionEnum;
import com.inteink.modules.biz.model.enums.RodLogTypeEnum;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.model.vo.DashboardVO;
import com.inteink.modules.biz.model.vo.DeviceInfoVO;
import com.inteink.modules.biz.model.vo.LiftingRodVO;
import com.inteink.modules.biz.service.DashboardService;
import com.inteink.modules.biz.service.LiftingRodService;
import com.inteink.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

/**
 * 道闸杆管理：增删改查 + 手动升降。
 */
@Api(tags = "道闸杆管理")
@RestController
@RequestMapping("/biz/rod")
@RequiredArgsConstructor
public class LiftingRodController extends AbstractController {

    private final LiftingRodService liftingRodService;
    private final DashboardService dashboardService;

    @ApiOperation("分页查询杆")
    @ApiOperationSupport(order = 1)
    @GetMapping("/page")
    @RequiresPermissions("biz:liftingrod:list")
    public Result<PageUtils> page(LiftingRodForm form) {
        return Result.ok(liftingRodService.queryPage(form));
    }

    @ApiOperation("杆详情")
    @ApiOperationSupport(order = 2)
    @GetMapping("/detail/{rodId}")
    @RequiresPermissions("biz:liftingrod:info")
    public Result<LiftingRodVO> detail(@PathVariable("rodId") Long rodId) {
        return Result.ok(liftingRodService.detail(rodId));
    }

    @ApiOperation("新增杆")
    @ApiOperationSupport(order = 3)
    @PostMapping("/save")
    @RequiresPermissions("biz:liftingrod:save")
    public Result<Long> save(@RequestBody BizLiftingRod rod) {
        return Result.ok(liftingRodService.saveRod(rod, getUserId()), "新增成功");
    }

    @ApiOperation("修改杆")
    @ApiOperationSupport(order = 4)
    @PostMapping("/update")
    @RequiresPermissions("biz:liftingrod:update")
    public Result update(@RequestBody BizLiftingRod rod) {
        liftingRodService.updateRod(rod);
        return Result.ok();
    }

    @ApiOperation("删除杆（逻辑删除）")
    @ApiOperationSupport(order = 5)
    @PostMapping("/remove/{rodId}")
    @RequiresPermissions("biz:liftingrod:delete")
    public Result remove(@PathVariable("rodId") Long rodId) {
        liftingRodService.removeRod(rodId);
        return Result.ok();
    }

    @ApiOperation("手动升杆")
    @ApiOperationSupport(order = 6)
    @PostMapping("/lift/{rodId}")
    @RequiresPermissions("biz:liftingrod:update")
    public Result lift(@PathVariable("rodId") Long rodId) {
        liftingRodService.operateRod(rodId, RodActionEnum.UP.getCode(), RodLogTypeEnum.MANUAL, null);
        return Result.ok(null, "升杆成功");
    }

    @ApiOperation("手动降杆")
    @ApiOperationSupport(order = 7)
    @PostMapping("/lower/{rodId}")
    @RequiresPermissions("biz:liftingrod:update")
    public Result lower(@PathVariable("rodId") Long rodId) {
        liftingRodService.operateRod(rodId, RodActionEnum.DOWN.getCode(), RodLogTypeEnum.MANUAL, null);
        return Result.ok(null, "降杆成功");
    }

    @ApiOperation("对账（拉设备真实态校正）")
    @PostMapping("/reconcile/{rodId}")
    @RequiresPermissions("biz:liftingrod:update")
    public Result<DeviceInfoVO> reconcile(@PathVariable("rodId") Long rodId) {
        return Result.ok(liftingRodService.reconcile(rodId), "对账完成");
    }

    @ApiOperation("看板实时状态聚合")
    @GetMapping("/liveStatus")
    @RequiresPermissions("biz:liftingrod:list")
    public Result<DashboardVO> liveStatus() {
        return Result.ok(dashboardService.dashboard());
    }
}
