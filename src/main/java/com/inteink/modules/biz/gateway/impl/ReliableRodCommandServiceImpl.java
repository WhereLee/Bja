package com.inteink.modules.biz.gateway.impl;

import com.inteink.modules.biz.gateway.DeviceResult;
import com.inteink.modules.biz.gateway.ReliableRodCommandService;
import com.inteink.modules.biz.gateway.RodCommandGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可靠下发实现：
 * - 同杆"进行中"守卫（防手动/自动/补跑并发连发）；
 * - 成功后短窗去重（同杆同动作窗口内跳过）；
 * - 仅对 isRetryable(TIMEOUT/OFFLINE) 做有限次指数退避重试。
 * 幂等为进程内（单机假设）；多实例需换分布式锁/存储。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReliableRodCommandServiceImpl implements ReliableRodCommandService {

    private final RodCommandGateway gateway;

    @Value("${biz.device.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${biz.device.retry.base-backoff-ms:300}")
    private long baseBackoffMs;

    @Value("${biz.device.dedup-window-ms:3000}")
    private long dedupWindowMs;

    /** 同杆"进行中"标记 */
    private final ConcurrentHashMap<Long, Boolean> inFlight = new ConcurrentHashMap<>();
    /** 同杆上次成功动作（去重窗内跳过重复） */
    private final ConcurrentHashMap<Long, LastSuccess> lastSuccess = new ConcurrentHashMap<>();

    private static class LastSuccess {
        final Integer action;
        final long ts;
        final String cmdId;
        LastSuccess(Integer action, long ts, String cmdId) {
            this.action = action;
            this.ts = ts;
            this.cmdId = cmdId;
        }
    }

    @Override
    public DeviceResult execute(Long rodId, Integer action) {
        if (inFlight.putIfAbsent(rodId, Boolean.TRUE) != null) {
            log.warn("rod={} 已有下发进行中，跳过重复", rodId);
            return DeviceResult.SKIPPED_DUPLICATE;
        }
        try {
            LastSuccess last = lastSuccess.get(rodId);
            long now = System.currentTimeMillis();
            if (last != null && Objects.equals(last.action, action) && now - last.ts < dedupWindowMs) {
                log.info("rod={} 动作{} 在{}ms去重窗内已成功(cmdId={})，跳过", rodId, action, dedupWindowMs, last.cmdId);
                return DeviceResult.SKIPPED_DUPLICATE;
            }

            String cmdId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            DeviceResult result = null;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                result = gateway.send(rodId, action);
                if (result == DeviceResult.SUCCESS || !result.isRetryable()) {
                    break;
                }
                if (attempt < maxAttempts) {
                    long backoff = baseBackoffMs * (1L << (attempt - 1));
                    log.warn("rod={} cmdId={} 第{}次下发={}，退避{}ms后重试", rodId, cmdId, attempt, result, backoff);
                    sleep(backoff);
                }
            }
            if (result == DeviceResult.SUCCESS) {
                lastSuccess.put(rodId, new LastSuccess(action, System.currentTimeMillis(), cmdId));
            }
            log.info("rod={} cmdId={} 最终结果={}（尝试<= {} 次）", rodId, cmdId, result, maxAttempts);
            return result;
        } finally {
            inFlight.remove(rodId);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
