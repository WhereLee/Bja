package com.inteink.modules.biz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 看板探测用的有界线程池：把"逐台串行探测"变"并发但有上限"，
 * 既降延迟又把并发连接数封顶（避免 fd 暴涨）。线程为守护线程，容器关闭即回收。
 */
@Configuration
public class DeviceProbeExecutorConfig {

    @Value("${biz.dashboard.probe-threads:32}")
    private int probeThreads;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService deviceProbeExecutor() {
        ThreadFactory tf = r -> {
            Thread t = new Thread(r, "dev-probe");
            t.setDaemon(true);
            return t;
        };
        return Executors.newFixedThreadPool(probeThreads, tf);
    }
}
