package com.inteink.modules.biz.utils;

import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import lombok.extern.slf4j.Slf4j;

/**
 * 升降杆重试工具类（全日志打印版）
 */
@Slf4j
public class RodRetryUtil {
    private static final long RETRY_DELAY_MS = 5 * 60 * 1000L; // 5分钟
    private static final int MAX_RETRY_COUNT = 1;

    public static boolean executeWithRetry(Long rodId, Integer actionCode, Long operator, LiftingRodService rodService) {
        int retryCount = 0;
        boolean executeSuccess = false;
        System.out.println("【RodRetryUtil】开始重试逻辑，杆ID：" + rodId + "，动作：" + (actionCode == 1 ? "升杆" : "降杆"));
        log.info("【RodRetryUtil】开始重试逻辑，杆ID：{}，动作：{}", rodId, actionCode == 1 ? "升杆" : "降杆");

        while (retryCount <= MAX_RETRY_COUNT) {
            try {
                BizLiftingRod rod = rodService.getById(rodId);
                System.out.println("【RodRetryUtil】第" + (retryCount + 1) + "次重试，查询杆信息：" + (rod == null ? "杆不存在" : "在线状态：" + rod.getRodOffline()));
                log.info("【RodRetryUtil】第{}次重试，查询杆信息：{}", retryCount + 1, rod == null ? "杆不存在" : "在线状态：" + rod.getRodOffline());

                if (rod == null) {
                    System.out.println("【RodRetryUtil】杆不存在，终止重试");
                    log.warn("【RodRetryUtil】杆不存在，终止重试");
                    break;
                }

                Boolean isOnline = rod.getRodOffline();
                if (Boolean.FALSE.equals(isOnline)) {
                    if (retryCount < MAX_RETRY_COUNT) {
                        System.out.println("【RodRetryUtil】杆离线，等待" + (RETRY_DELAY_MS / 60000) + "分钟后重试");
                        log.warn("【RodRetryUtil】杆离线，等待{}分钟后重试", RETRY_DELAY_MS / 60000);
                        Thread.sleep(RETRY_DELAY_MS);
                        retryCount++;
                        continue;
                    } else {
                        System.out.println("【RodRetryUtil】重试次数用尽，终止重试");
                        log.error("【RodRetryUtil】重试次数用尽，终止重试");
                        break;
                    }
                }

                // 杆在线，执行操作
                System.out.println("【RodRetryUtil】杆在线，执行升降操作");
                log.info("【RodRetryUtil】杆在线，执行升降操作");
                rodService.liftRod(rodId, actionCode, operator);
                executeSuccess = true;
                System.out.println("【RodRetryUtil】重试成功，杆ID：" + rodId);
                log.info("【RodRetryUtil】重试成功，杆ID：{}", rodId);
                break;

            } catch (InterruptedException e) {
                String errorMsg = "重试延迟被中断：" + e.getMessage();
                System.err.println("【RodRetryUtil】错误：" + errorMsg);
                log.error("【RodRetryUtil】错误：{}", errorMsg, e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                String errorMsg = "第" + (retryCount + 1) + "次重试异常：" + e.getMessage();
                System.err.println("【RodRetryUtil】错误：" + errorMsg);
                log.error("【RodRetryUtil】错误：{}", errorMsg, e);
                if (retryCount < MAX_RETRY_COUNT) {
                    retryCount++;
                    try {
                        System.out.println("【RodRetryUtil】等待" + (RETRY_DELAY_MS / 60000) + "分钟后重试");
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        System.err.println("【RodRetryUtil】重试延迟被中断：" + ie.getMessage());
                        log.error("【RodRetryUtil】重试延迟被中断：{}", ie.getMessage(), ie);
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.out.println("【RodRetryUtil】重试次数用尽，终止重试");
                    log.error("【RodRetryUtil】重试次数用尽，终止重试");
                    break;
                }
            }
        }
        System.out.println("【RodRetryUtil】重试逻辑结束，结果：" + (executeSuccess ? "成功" : "失败"));
        log.info("【RodRetryUtil】重试逻辑结束，结果：{}", executeSuccess ? "成功" : "失败");
        return executeSuccess;
    }
}