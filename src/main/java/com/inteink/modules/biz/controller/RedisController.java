package com.inteink.modules.biz.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.utils.RedisUtils;
import com.inteink.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 从redis中取缓存
 */
@Api(tags = "异步接口-获取操作结果",position = 900)
@RestController
@RequestMapping("dev/redis")
public class RedisController {
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 查询设备操作进度
     * 操作中：返回{"code":1,"msg":"执行中...","data":null}
     * 操作完成·失败：返回{"code":4,"msg":"失败原因","data":null}
     * 操作完成·成功：返回{"code":0,"msg":"success","data":"消息内容"}
     * @param secquence
     * @return
     */
    @ApiOperation(value = "查询操作进度",notes = "异步接口-获取操作结果 不设置权限")
    @ApiOperationSupport(order = 1)
    @GetMapping("/get/{secquence}")
    public Object get(@PathVariable("secquence") String secquence){
        //1.读取Redis
        Object obj = redisUtils.getKeyValue(secquence);

        if (obj != null) {//执行完成了
            return obj;
        } else {
            return Result.error(1,"操作中，请等待......","操作中，请等待......");
        }
    }

}
