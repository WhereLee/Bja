package com.inteink.modules.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.vo.DeviceInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 设备态快照缓存：后台每 5s 用有界线程池并发探测一轮所有绑定设备，写入内存快照；
 * 看板只读快照 → 请求成本从“N 次 HTTP”降为“N 次内存读”，与并发量解耦。
 * 纯进程内（单机假设），不引 Redis/新依赖。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceStateCache {

    private static final long STATUS_VALID = 0L;
    private static final long PROBE_TIMEOUT_MS = 3000L;

    private final BizConverterMapper converterMapper;
    private final DeviceQueryService deviceQueryService;
    private final ExecutorService deviceProbeExecutor;

    /** 快照：rodId -> 设备态 */
    private volatile Map<Long, DevSnapshot> snapshots = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "dev-state-cache");
                t.setDaemon(true);
                return t;
            });

    public record DevSnapshot(boolean online, Integer state, String stateDesc) {
    }

    @PostConstruct
    void start() {
        safeRefresh();
        scheduler.scheduleWithFixedDelay(this::safeRefresh, 5, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    void stop() {
        scheduler.shutdownNow();
    }

    public Map<Long, DevSnapshot> snapshot() {
        return snapshots;
    }

    /** 强制现场刷新并返回（压测 A/B 用：cache=false 时每请求都真探测）。 */
    public Map<Long, DevSnapshot> probeNow() {
        safeRefresh();
        return snapshots;
    }

    private void safeRefresh() {
        try {
            refresh();
        } catch (Exception e) {
            log.warn("设备态缓存刷新失败：{}", e.getMessage());
        }
    }

    private void refresh() {
        List<BizConverter> convs = converterMapper.selectList(new LambdaQueryWrapper<BizConverter>()
                .isNotNull(BizConverter::getRodId)
                .eq(BizConverter::getConverterStatus, STATUS_VALID));
        Map<Long, DevSnapshot> next = new ConcurrentHashMap<>();
        List<Future<?>> fs = new ArrayList<>();
        for (BizConverter c : convs) {
            Long rid = c.getRodId();
            if (rid == null) {
                continue;
            }
            Long cid = c.getConverterId();
            fs.add(deviceProbeExecutor.submit(() -> {
                DeviceInfoVO v = deviceQueryService.probe(cid);
                boolean on = v != null && Boolean.TRUE.equals(v.getOnline());
                next.put(rid, on ? new DevSnapshot(true, v.getState(), v.getStateDesc())
                        : new DevSnapshot(false, null, null));
            }));
        }
        for (Future<?> f : fs) {
            try {
                f.get(PROBE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                f.cancel(true);
            }
        }
        this.snapshots = next;
        log.debug("设备态快照刷新完成：{} 台", next.size());
    }
}
