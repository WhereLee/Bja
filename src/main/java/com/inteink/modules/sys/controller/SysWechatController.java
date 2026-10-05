package com.inteink.modules.sys.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.annotation.SysLog;
import com.inteink.common.utils.Result;
import com.inteink.modules.sys.entity.SysDicWechatEntity;
import com.inteink.modules.sys.service.SysDictionaryService;
import com.inteink.modules.sys.vo.SysDicWechatVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "微信小程序管理",position = 249)
@RestController
@RequestMapping("sys/wechat")
public class SysWechatController {
    @Autowired
    private SysDictionaryService sysDictionaryService;

    /**
     * 信息
     */
    @ApiOperation(value = "详细查询",notes = "查询微信小程序配置；权限说明：sys:wechat:info 查询")
    @ApiOperationSupport(order = 20)
    @SysLog(module = "微信小程序模块",func = "查询",value = "查询微信小程序配置")
    @GetMapping("/info")
    @RequiresPermissions("sys:wechat:info")
    public Result<SysDicWechatEntity> info(){
        SysDicWechatEntity wechat = sysDictionaryService.getWechat();

        return Result.ok(wechat);
    }

    /**
     * 修改
     */
    @ApiOperation(value = "配置接口",notes = "配置微信小程序，权限说明：sys:wechat:update 修改")
    @ApiOperationSupport(order = 50)
    @SysLog(module = "微信小程序模块",func = "配置",value = "配置微信小程序")
    @PostMapping("/update")
    @RequiresPermissions("sys:wechat:update")
    public Result update(@RequestBody SysDicWechatVO wechatVO){
        sysDictionaryService.setWechat(wechatVO);

        return Result.ok();
    }
}
