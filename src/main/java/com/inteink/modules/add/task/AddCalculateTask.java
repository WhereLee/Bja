// add/task/AddCalculateTask.java
package com.inteink.modules.add.task;

import com.inteink.modules.job.task.ITask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 定时计算1+1的任务（add包下）
 * 每天21点执行
 */
@Slf4j
@Component("addCalculateTask") // Spring Bean名称，配置任务时需要使用
public class AddCalculateTask implements ITask {

    @Override
    public void run(String params) {
        // 计算1+1
        int result = 1 + 1;
        // 输出结果到日志
        log.info("【add包定时任务】每天23.55点计算1+1的结果：{}", result);
    }
}