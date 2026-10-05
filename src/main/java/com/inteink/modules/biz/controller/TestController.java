package com.inteink.modules.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.inteink.common.utils.LinkedListUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.queue.LedQueueHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private LedQueueHandler ledQueueHandler;

    /**
     * 当前队列情况
     * @return
     */
    @GetMapping("/queueInfo")
    public Result getQueueInfo(){
        Map<Long, LinkedListUtils<JSONObject>> map = ledQueueHandler.getQueueInfo();

        return Result.ok(map);
    }

    /**
     * 当前队列情况
     * @return
     */
    @GetMapping("/activeLedTask")
    public Result getActiveLedTask(@RequestParam("ledIdList") List<Long> ledIdList){
        List<JSONObject> activeLedList = ledQueueHandler.getActiveLedTask(ledIdList);

        return Result.ok(activeLedList);
    }

    /**
     * 下发测试
     * @param object
     * @return
     */
    @PostMapping("/distribute")
    public Result distribute(@RequestBody JSONObject object) {
        //LED
        Long ledId = object.getLong("ledId");
        //10：添加到队列队尾  11：添加到队列队首
        Integer type = object.getInteger("type");

        //下发任务
        Boolean flag = ledQueueHandler.startOrExist(type, ledId, object);
        if (flag)
            ledQueueHandler.ledDistributeTask(ledId);

        return Result.ok(flag);
    }
}
