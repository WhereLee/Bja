package com.inteink.modules.biz.gateway.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.gateway.DeviceResult;
import com.inteink.modules.biz.gateway.RodCommandGateway;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;

/**
 * HTTP 按设备地址下发，并把结果分类为 DeviceResult。
 * 分类：连不上/503 -> OFFLINE；读超时 -> TIMEOUT；500 或状态不符 -> DEVICE_REJECT；200 且 state==action -> SUCCESS。
 * 仅当 biz.device.mode=http 时启用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "biz.device", name = "mode", havingValue = "http")
public class HttpRodCommandGateway implements RodCommandGateway {

    private final BizConverterMapper converterMapper;

    @Value("${biz.device.timeout-ms:2000}")
    private long timeoutMs;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3)).build();

    @Override
    public DeviceResult send(Long rodId, Integer action) {
        BizConverter converter = findConverter(rodId);
        if (converter == null) {
            log.warn("【设备下发】rod={} 未绑定可用转换器，判 OFFLINE", rodId);
            return DeviceResult.OFFLINE;
        }
        String url = "http://" + converter.getConverterIp() + ":" + converter.getConverterPort() + "/command";
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"action\":" + action + "}"))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            DeviceResult result = classifyResponse(resp, action);
            log.info("【设备下发】rod={} sn={} {} action={} -> {}",
                    rodId, converter.getConverterSn(), url, action, result);
            return result;
        } catch (Exception e) {
            DeviceResult result = classifyException(e);
            log.warn("【设备下发】rod={} sn={} {} 异常分类={}（{}）",
                    rodId, converter.getConverterSn(), url, result, e.toString());
            return result;
        }
    }

    private DeviceResult classifyResponse(HttpResponse<String> resp, Integer action) {
        int code = resp.statusCode();
        if (code == 503) {
            return DeviceResult.OFFLINE;
        }
        if (code != 200) {
            return DeviceResult.DEVICE_REJECT;
        }
        JSONObject o = JSON.parseObject(resp.body());
        Integer state = o == null ? null : o.getInteger("state");
        return (state != null && state.equals(action)) ? DeviceResult.SUCCESS : DeviceResult.DEVICE_REJECT;
    }

    private DeviceResult classifyException(Exception e) {
        if (e instanceof HttpTimeoutException) {
            return DeviceResult.TIMEOUT;
        }
        if (e instanceof ConnectException) {
            return DeviceResult.OFFLINE;
        }
        if (e instanceof java.net.SocketTimeoutException) {
            return DeviceResult.TIMEOUT;
        }
        return DeviceResult.OFFLINE;
    }

    private BizConverter findConverter(Long rodId) {
        if (rodId == null) {
            return null;
        }
        List<BizConverter> list = converterMapper.selectList(new LambdaQueryWrapper<BizConverter>()
                .eq(BizConverter::getRodId, rodId)
                .eq(BizConverter::getConverterStatus, 0L)
                .orderByAsc(BizConverter::getConverterId));
        return list.isEmpty() ? null : list.get(0);
    }
}
