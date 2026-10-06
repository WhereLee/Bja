package com.inteink.modules.biz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启用 Spring 定时任务（供定期对账等轻量后台任务使用；业务定时仍走 Quartz）。
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
