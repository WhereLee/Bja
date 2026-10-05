package com.inteink.modules.sys.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.annotation.SysLog;
import com.inteink.common.utils.Result;
import com.inteink.modules.sys.entity.SysDicQyweixinEntity;
import com.inteink.modules.sys.service.SysDictionaryService;
import com.inteink.modules.sys.vo.SysDicQyweixinVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "企业微信管理",position = 248)
@RestController
@RequestMapping("sys/qyweixin")
public class SysQyweixinController {
    @Autowired
    private SysDictionaryService sysDictionaryService;

    /**
     * 信息
     */
    @ApiOperation(value = "详细查询",notes = "查询企业微信配置；权限说明：sys:qyweixin:info 查询")
    @ApiOperationSupport(order = 20)
    @SysLog(module = "企业微信模块",func = "查询",value = "查询企业微信配置")
    @GetMapping("/info")
    @RequiresPermissions("sys:qyweixin:info")
    public Result<SysDicQyweixinEntity> info(){
        SysDicQyweixinEntity qyweixin = sysDictionaryService.getQyweixin();

        return Result.ok(qyweixin);
    }

    /**
     * 修改
     */
    @ApiOperation(value = "配置接口",notes = "配置企业微信，权限说明：sys:qyweixin:update 修改")
    @ApiOperationSupport(order = 50)
    @SysLog(module = "企业微信模块",func = "配置",value = "配置企业微信")
    @PostMapping("/update")
    @RequiresPermissions("sys:qyweixin:update")
    public Result update(@RequestBody SysDicQyweixinVO qyweixinVO){
        sysDictionaryService.setQyweixin(qyweixinVO);

        return Result.ok();
    }
}
