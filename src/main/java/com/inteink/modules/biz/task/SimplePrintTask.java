package com.inteink.modules.biz.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.inteink.modules.job.task.ITask; // 对齐你的ITask接口路径

/**
 * 极简测试Task（控制台必打印）
 */
@Slf4j
@Component("simplePrintTask") // Bean名称：simplePrintTask（与请求数据中的jobBean一致）
public class SimplePrintTask implements ITask {

    @Override
    public void run(String params) {
        // 方式1：日志输出（INFO级别，确保不被过滤）
        log.info("========== 【SimplePrintTask】定时任务执行 ==========");
        log.info("执行时间：{}", System.currentTimeMillis());
        log.info("传入参数：{}", params);
        log.info("==================================================");

        // 方式2：强制控制台输出（绕开日志框架，必打印）
        System.out.println("========== 【SimplePrintTask】System.out强制输出 ==========");
        System.out.println("执行时间：" + System.currentTimeMillis());
        System.out.println("传入参数：" + params);
        System.out.println("==================================================");
    }
}