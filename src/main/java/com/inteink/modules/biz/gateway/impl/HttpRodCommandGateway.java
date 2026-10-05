package com.inteink.modules.biz.gateway.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inteink.modules.biz.gateway.RodCommandGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 通过 HTTP 调用（云端/本地的）虚拟道闸设备完成一次下发。
 * 仅当 biz.device.mode=http 时启用；返回体里的 state 与请求 action 一致才算成功。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "biz.device", name = "mode", havingValue = "http")
public class HttpRodCommandGateway implements RodCommandGateway {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3)).build();

    @Value("${biz.device.url:http://127.0.0.1:18080}")
    private String baseUrl;

    @Override
    public boolean send(Long rodId, Integer action) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/command"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"action\":" + action + "}"))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                log.warn("【虚拟设备】下发失败 rodId={} action={} http={}", rodId, action, resp.statusCode());
                return false;
            }
            JSONObject o = JSON.parseObject(resp.body());
            Integer state = o == null ? null : o.getInteger("state");
            boolean ok = state != null && state.equals(action);
            log.info("【虚拟设备】rodId={} action={} 设备返回state={} -> {}", rodId, action, state, ok ? "成功" : "不一致");
            return ok;
        } catch (Exception e) {
            log.error("【虚拟设备】下发异常 rodId=" + rodId + " action=" + action, e);
            return false;
        }
    }
}
