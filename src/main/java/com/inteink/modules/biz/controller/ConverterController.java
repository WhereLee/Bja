package com.inteink.modules.biz.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.form.ConverterForm;
import com.inteink.modules.biz.model.vo.DeviceInfoVO;
import com.inteink.modules.biz.service.ConverterService;
import com.inteink.modules.biz.service.DeviceQueryService;
import com.inteink.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

/**
 * 转换器管理：设备台账 CRUD + 与杆绑定/解绑。
 */
@Api(tags = "转换器管理")
@RestController
@RequestMapping("/biz/converter")
@RequiredArgsConstructor
public class ConverterController extends AbstractController {

    private final ConverterService converterService;
    private final DeviceQueryService deviceQueryService;

    @ApiOperation("分页查询转换器")
    @ApiOperationSupport(order = 1)
    @GetMapping("/page")
    @RequiresPermissions("biz:converter:list")
    public Result<PageUtils> page(ConverterForm form) {
        return Result.ok(converterService.queryPage(form));
    }

    @ApiOperation("新增转换器")
    @ApiOperationSupport(order = 2)
    @PostMapping("/save")
    @RequiresPermissions("biz:converter:save")
    public Result<Long> save(@RequestBody BizConverter converter) {
        return Result.ok(converterService.saveConverter(converter, getUserId()), "新增成功");
    }

    @ApiOperation("修改转换器")
    @ApiOperationSupport(order = 3)
    @PostMapping("/update")
    @RequiresPermissions("biz:converter:update")
    public Result update(@RequestBody BizConverter converter) {
        converterService.updateConverter(converter);
        return Result.ok();
    }

    @ApiOperation("删除转换器（逻辑删除）")
    @ApiOperationSupport(order = 4)
    @PostMapping("/remove/{converterId}")
    @RequiresPermissions("biz:converter:delete")
    public Result remove(@PathVariable("converterId") Long converterId) {
        converterService.removeConverter(converterId);
        return Result.ok();
    }

    @ApiOperation("绑定到杆")
    @ApiOperationSupport(order = 5)
    @PostMapping("/bind/{converterId}/{rodId}")
    @RequiresPermissions("biz:converter:update")
    public Result bind(@PathVariable("converterId") Long converterId, @PathVariable("rodId") Long rodId) {
        converterService.bind(converterId, rodId);
        return Result.ok(null, "绑定成功");
    }

    @ApiOperation("解绑")
    @ApiOperationSupport(order = 6)
    @PostMapping("/unbind/{converterId}")
    @RequiresPermissions("biz:converter:update")
    public Result unbind(@PathVariable("converterId") Long converterId) {
        converterService.unbind(converterId);
        return Result.ok(null, "解绑成功");
    }

    @ApiOperation("查询设备实时状态（按地址探测）")
    @ApiOperationSupport(order = 7)
    @GetMapping("/device/{converterId}")
    @RequiresPermissions("biz:converter:info")
    public Result<DeviceInfoVO> device(@PathVariable("converterId") Long converterId) {
        return Result.ok(deviceQueryService.probe(converterId));
    }
}
