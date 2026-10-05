package com.inteink.modules.sys.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.annotation.SysLog;
import com.inteink.common.utils.Result;
import com.inteink.modules.sys.entity.SysDicIplistEntity;
import com.inteink.modules.sys.service.SysDictionaryService;
import com.inteink.modules.sys.vo.SysDicIplistVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "IP黑白名单管理",position = 246)
@RestController
@RequestMapping("sys/iplist")
public class SysIplistController {
    @Autowired
    private SysDictionaryService sysDictionaryService;

    /**
     * 信息
     */
    @ApiOperation(value = "详细查询",notes = "查询IP黑白名单配置；权限说明：sys:iplist:info 查询")
    @ApiOperationSupport(order = 20)
    @SysLog(module = "IP黑白名单模块",func = "查询",value = "查询IP黑白名单配置")
    @GetMapping("/info")
    @RequiresPermissions("sys:iplist:info")
    public Result<SysDicIplistEntity> info(){
        SysDicIplistEntity iplist = sysDictionaryService.getIpList();

        return Result.ok(iplist);
    }

    /**
     * 修改
     */
    @ApiOperation(value = "配置接口",notes = "配置IP黑白名单，权限说明：sys:iplist:update 修改")
    @ApiOperationSupport(order = 50)
    @SysLog(module = "IP黑白名单模块",func = "配置",value = "配置IP黑白名单")
    @PostMapping("/update")
    @RequiresPermissions("sys:iplist:update")
    public Result update(@RequestBody SysDicIplistVO iplistVO){
        sysDictionaryService.setIpList(iplistVO);

        return Result.ok();
    }
}
