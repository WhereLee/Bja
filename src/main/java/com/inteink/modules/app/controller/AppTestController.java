/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.modules.app.controller;


import com.inteink.common.utils.Result;
import com.inteink.modules.app.annotation.Login;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APP测试接口
 *
 * @author Mark sunlightcs@gmail.com
 */

@Api(tags = "APP接口测试",position = 999)
@RestController
@RequestMapping("/app")
//@Api("APP测试接口")
public class AppTestController{

    @Login
    @GetMapping("userId")
    //@ApiOperation("获取用户ID")
    public Result userInfo(@RequestAttribute("userId") Integer userId){
        return Result.ok(userId);
    }

    @ApiOperation(value = "测试0",notes = "测试0")
    @GetMapping("notToken")
    //@ApiOperation("忽略Token验证测试")
    public Result notToken(){
        return Result.ok("无需token也能访问。。。");
    }

}
