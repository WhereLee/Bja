package com.inteink.modules.sys.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.utils.Result;
import com.inteink.modules.sys.form.SysLogForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.inteink.modules.sys.entity.SysLogEntity;
import com.inteink.modules.sys.service.SysLogService;
import com.inteink.common.utils.PageUtils;



/**
 * 操作日志 查询操作不记日志
 *
 * @author wll
 * @email 
 * @date 2020-04-22 15:02:49
 */
@Api(tags = "操作日志",position = 250)
@RestController
@RequestMapping("sys/log")
public class SysLogController extends AbstractController{
    @Autowired
    private SysLogService sysLogService;

    /**
     * 列表
     */
    @ApiOperation(value = "列表查询",notes = "查询所有操作日志；权限说明：sys:log:list")
    @ApiOperationSupport(order = 10,ignoreParameters = {"areaId","sqlFilter","logState"})
    @GetMapping("/list")
    @RequiresPermissions("sys:log:list")
    public Result<PageUtils> list(SysLogForm form){
        form.setLogState(0);
        PageUtils page = sysLogService.queryPage(form,getUserId());

        return Result.ok(page);
    }


    /**
     * 信息
     */
    /*@ApiOperation(value = "详细查询",notes = "查询所选操作日志的详细信息，权限说明：sys:log:info 查询")
    @ApiOperationSupport(order = 30)
    @GetMapping("/info/{logId}")
    @RequiresPermissions("sys:log:info")
    public Result<SysLogEntity> info(@PathVariable("logId") Long logId){
		SysLogEntity log = sysLogService.getInfo(logId);

        return Result.ok(log);
    }*/


}
