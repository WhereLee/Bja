package com.inteink.modules.sys.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.annotation.SysLog;
import com.inteink.common.utils.Result;
import com.inteink.modules.sys.entity.SysDicSubmailEntity;
import com.inteink.modules.sys.service.SysDictionaryService;
import com.inteink.modules.sys.vo.SysDicSubmailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "赛迪云短信管理",position = 248)
@RestController
@RequestMapping("sys/submail")
public class SysDicSubmailController {
    @Autowired
    private SysDictionaryService sysDictionaryService;

    /**
     * 信息
     */
    @ApiOperation(value = "详细查询",notes = "查询短信配置；权限说明：sys:submail:info 查询")
    @ApiOperationSupport(order = 20)
    @SysLog(module = "短信模块",func = "查询",value = "查询短信配置")
    @GetMapping("/info")
    @RequiresPermissions("sys:submail:info")
    public Result<SysDicSubmailEntity> info(){
        SysDicSubmailEntity submail = sysDictionaryService.getSubmail();

        return Result.ok(submail);
    }

    /**
     * 修改
     */
    @ApiOperation(value = "配置接口",notes = "配置短信，权限说明：sys:submail:update 修改")
    @ApiOperationSupport(order = 50)
    @SysLog(module = "短信模块",func = "配置",value = "配置短信")
    @PostMapping("/update")
    @RequiresPermissions("sys:submail:update")
    public Result update(@RequestBody SysDicSubmailVO submailVO){
        sysDictionaryService.setSubmail(submailVO);

        return Result.ok();
    }
}
