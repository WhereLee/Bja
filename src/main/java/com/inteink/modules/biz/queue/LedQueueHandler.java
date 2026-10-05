package com.inteink.modules.biz.queue;

import com.alibaba.fastjson.JSONObject;
import com.inteink.common.utils.LinkedListUtils;
import com.inteink.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ledQueueHandler")
public class LedQueueHandler {
    @Autowired
    private RedisUtils redisUtils;

    private static String key = "active_led";

    //队列  <ledId, Object>
    private static Map<Long, LinkedListUtils<JSONObject>> queueMap = new HashMap<>();

    //将任务添加到队列 队尾
    public void putTaskLastIntoQueue(Long ledId, JSONObject object) {
        LinkedListUtils<JSONObject> queue = new LinkedListUtils<>();
        if (queueMap.containsKey(ledId))
            queue = queueMap.get(ledId);

        queue.addLast(object);
        queueMap.put(ledId, queue);
    }

    //将任务添加到队列 队首
    public void putTaskFirstIntoQueue(Long ledId, JSONObject object) {
        LinkedListUtils<JSONObject> queue = new LinkedListUtils<>();
        if (queueMap.containsKey(ledId))
            queue = queueMap.get(ledId);

        queue.addFirst(object);
        queueMap.put(ledId, queue);
    }

    /**
     * 当前队列情况
     * @return
     */
    public Map<Long, LinkedListUtils<JSONObject>> getQueueInfo() {
        return queueMap;
    }

    /**
     * 当前活跃的LED任务
     * @param ledIdList
     * @return
     */
    public List<JSONObject> getActiveLedTask(List<Long> ledIdList) {
        List<JSONObject> activeLedList = new ArrayList<>();
        for (Long ledId : ledIdList) {
            if (redisUtils.hasHashKey(key, ledId)) {
                Object value = redisUtils.getHashValue(key, ledId);

                activeLedList.add(new JSONObject() {{
                    put("ledId", ledId);put("value", value);
                }});
            }
        }

        return activeLedList;
    }

    /**
     * 支持并发
     * 1.添加任务（并判断是否开启LED下发任务）
     * 2.判断是否退出LED下发任务
     * @param type 10：添加到队列队尾  11：添加到队列队首  20：判断是否退出LED下发任务
     * @param ledId
     * @param object
     * @return true：开启LED下发任务 或 退出LED下发任务
     */
    public Boolean startOrExist (Integer type, Long ledId, JSONObject object) {
        synchronized (ledId) {
            //是否开启LED下发任务 或 是否退出LED下发任务
            Boolean flag = false;
            if (type == 10 || type == 11) {
                //判断是否开启LED下发任务
                if (!queueMap.containsKey(ledId))
                    flag = true;

                //加入队列
                if (type == 10)
                    putTaskLastIntoQueue(ledId, object);
                if (type == 11)
                    putTaskFirstIntoQueue(ledId, object);
            } else {
                if (!queueMap.containsKey(ledId))//这种情况，正常不会发生
                    flag = true;
                else {
                    //对应的队列
                    LinkedListUtils<JSONObject> queue = queueMap.get(ledId);
                    if (queue == null || queue.size() == 0) {
                        //清除对应LED的队列
                        queueMap.remove(ledId);
                        flag = true;
                    }
                }
            }

            return flag;
        }
    }


    /**
     * LED 下发
     * 问题：中途更新了默认图片——怎么处理
     * @param ledId
     */
    @Async
    public void ledDistributeTask(Long ledId) {
        log.info("开启LED下发任务:{}",Thread.currentThread().getId());
        //当前活跃的LED任务
        redisUtils.setHashValue(key, ledId, Thread.currentThread().getId());

        while(true) {
            try {
                //1.取队列
                if (!queueMap.containsKey(ledId)) {
                    //暂停500ms
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {}

                    //没有对应队列  删除当前活跃的LED任务 并退出 并退出
                    if (!startOrExist(11, ledId, null)) {
                        log.info("退出LED下发任务:{}",Thread.currentThread().getId());
                        redisUtils.deleteHashValue(key, ledId);
                        break;
                    }
                }
                //对应的队列
                LinkedListUtils<JSONObject> queue = queueMap.get(ledId);
                log.info("queue:{}",queue);

                //2.互动下发完成后，下发默认图片 然后退出
                if (queue == null || queue.size() == 0) {
                    //下发默认图片
                    log.info("下发默认图片");

                    if (!startOrExist(11, ledId, null)) {
                        //删除当前活跃的LED任务 并退出
                        log.info("退出LED下发任务:{}",Thread.currentThread().getId());
                        redisUtils.deleteHashValue(key, ledId);
                        break;
                    }
                }

                //3.下发互动图片
                JSONObject object = queue.QueryAndRemove();
                log.info("互动图片:{}",object);
                log.info("下发互动图片30秒");
                //暂停30s
                try {
                    Thread.sleep(30*1000);
                } catch (Exception e) {}

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
