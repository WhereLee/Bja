package com.inteink.modules.biz.controller;

import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.form.ConverterForm;
import com.inteink.modules.biz.service.lifting.ConverterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 仅基于你原有Service接口的Controller（适配分页+完整CRUD）
 */
@RestController
@RequestMapping("/biz/converter")
@Api(tags = "转换器管理")
public class ConverterController {

    @Autowired
    private ConverterService converterService;

    /**
     * 新增转换器（表单传参，支持绑定升降杆）
     */
    @ApiOperation("新增转换器")
    @PostMapping("/save")
    public Result save(
            @ApiParam("设备端口（0-65535）") @RequestParam(required = true) Integer converterPort,
            @ApiParam("设备SN编号") @RequestParam(required = true) String converterSn,
            @ApiParam("IP地址（IPv4）") @RequestParam(required = true) String converterIp,
            @ApiParam("绑定的升降杆ID") @RequestParam(required = true) Long rodId
    ) {
        try {
            BizConverter converter = new BizConverter();
            converter.setConverterPort(converterPort);
            converter.setConverterSn(converterSn);
            converter.setConverterIp(converterIp);
            converter.setRodId(rodId);
            converter.setConverterCreator(1001L); // 替换为你的登录人ID逻辑
            converter.setConverterCreatetime(System.currentTimeMillis() / 1000);
            converter.setConverterUpdatetime(System.currentTimeMillis() / 1000);
            converter.setConverterStatus(0L);

            boolean saveResult = converterService.saveConverter(converter);
            if (!saveResult) {
                return Result.error(500, "新增失败", null);
            }
            return Result.ok(converter, "新增成功，转换器ID：" + converter.getConverterId());
        } catch (Exception e) {
            return Result.error(500, "新增失败：" + e.getMessage(), null);
        }
    }

    /**
     * 修改转换器（支持改端口/IP/绑定升降杆ID）
     */
    @ApiOperation("修改转换器")
    @PostMapping("/update")
    public Result update(
            @ApiParam("转换器ID") @RequestParam(required = true) Long converterId,
            @ApiParam("新端口（可选）") @RequestParam(required = false) Integer converterPort,
            @ApiParam("新IP（可选）") @RequestParam(required = false) String converterIp,
            @ApiParam("新绑定升降杆ID（可选）") @RequestParam(required = false) Long rodId
    ) {
        try {
            BizConverter converter = new BizConverter();
            converter.setConverterId(converterId);
            // 按需赋值（传了才改）
            if (converterPort != null) converter.setConverterPort(converterPort);
            if (converterIp != null && !converterIp.isEmpty()) converter.setConverterIp(converterIp);
            if (rodId != null) converter.setRodId(rodId);

            boolean updateResult = converterService.updateConverter(converter);
            if (!updateResult) {
                return Result.error(500, "修改失败", null);
            }
            return Result.ok(converterService.getById(converterId), "修改成功");
        } catch (Exception e) {
            return Result.error(500, "修改失败：" + e.getMessage(), null);
        }
    }

    /**
     * 分页查询转换器（支持筛选升降杆ID）
     */
    @ApiOperation("分页查询转换器")
    @GetMapping("/page")
    public Result page(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("升降杆ID（筛选）") @RequestParam(required = false) Long rodId
    ) {
        try {
            ConverterForm form = new ConverterForm();
            form.setPageNum(pageNum);
            form.setPageSize(pageSize);
            form.setRodId(rodId);

            PageUtils page = converterService.queryPage(form);
            return Result.ok(page, "查询成功");
        } catch (Exception e) {
            return Result.error(500, "查询失败：" + e.getMessage(), null);
        }
    }

    /**
     * 删除转换器（逻辑删除）
     */
    @ApiOperation("删除转换器")
    @PostMapping("/delete")
    public Result delete(@ApiParam("转换器ID") @RequestParam(required = true) Long converterId) {
        try {
            BizConverter converter = new BizConverter();
            converter.setConverterId(converterId);
            converter.setConverterStatus(1L);
            converter.setConverterUpdatetime(System.currentTimeMillis() / 1000);

            boolean deleteResult = converterService.updateById(converter);
            if (!deleteResult) {
                return Result.error(500, "删除失败", null);
            }
            return Result.ok(null, "删除成功");
        } catch (Exception e) {
            return Result.error(500, "删除失败：" + e.getMessage(), null);
        }
    }
    // 新增：查询单条转换器（显示是否有效）
    @ApiOperation("查询单条转换器（含有效性标识）")
    @GetMapping("/getById")
    public Result getById(
            @ApiParam("转换器ID") @RequestParam(required = true) Long converterId
    ) {
        try {
            BizConverter converter = converterService.getConverterById(converterId);
            if (converter == null) {
                return Result.error(404, "转换器不存在", null);
            }

            // 封装结果：新增“是否有效”“状态描述”字段
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("converter", converter); // 原转换器数据
            resultMap.put("isValid", converter.getConverterStatus() == 0); // true=有效，false=无效
            resultMap.put("statusDesc", converter.getConverterStatus() == 0 ? "正常" : "已删除"); // 状态描述

            return Result.ok(resultMap, "查询成功");
        } catch (Exception e) {
            return Result.error(500, "查询失败：" + e.getMessage(), null);
        }
    }
}