package com.inteink.modules.biz.task;

import com.inteink.modules.biz.service.LiftingRodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定期对账：以设备回报的真实态为准，校正库内 rod_state / rod_offline 的漂移。
 * 默认每 2 分钟一轮，可用 biz.reconcile.enabled=false 关闭。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "biz.reconcile", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ReconcileJob {

    private final LiftingRodService liftingRodService;

    @Scheduled(fixedDelayString = "${biz.reconcile.interval-ms:120000}", initialDelay = 30000)
    public void reconcile() {
        try {
            int changed = liftingRodService.reconcileAll();
            if (changed > 0) {
                log.info("【定期对账】校正 {} 根杆状态", changed);
            }
        } catch (Exception e) {
            log.warn("【定期对账】失败：{}", e.getMessage());
        }
    }
}
