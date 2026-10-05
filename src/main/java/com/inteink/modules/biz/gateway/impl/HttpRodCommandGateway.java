package com.inteink.modules.biz.gateway.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inteink.modules.biz.gateway.RodCommandGateway;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * 通过 HTTP 按设备地址下发指令：rod -> 绑定的转换器 -> ip:port -> POST /command。
 * 仅当 biz.device.mode=http 时启用；设备返回的 state 与请求 action 一致才算成功。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "biz.device", name = "mode", havingValue = "http")
public class HttpRodCommandGateway implements RodCommandGateway {

    private final BizConverterMapper converterMapper;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3)).build();

    @Override
    public boolean send(Long rodId, Integer action) {
        BizConverter converter = findConverter(rodId);
        if (converter == null) {
            log.warn("【设备下发】rod={} 未绑定可用转换器，下发失败", rodId);
            return false;
        }
        String url = "http://" + converter.getConverterIp() + ":" + converter.getConverterPort() + "/command";
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"action\":" + action + "}"))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                log.warn("【设备下发】rod={} sn={} {} 返回 http={}",
                        rodId, converter.getConverterSn(), url, resp.statusCode());
                return false;
            }
            JSONObject o = JSON.parseObject(resp.body());
            Integer state = o == null ? null : o.getInteger("state");
            boolean ok = state != null && state.equals(action);
            log.info("【设备下发】rod={} sn={} {} action={} 设备state={} -> {}",
                    rodId, converter.getConverterSn(), url, action, state, ok ? "成功" : "不一致");
            return ok;
        } catch (Exception e) {
            log.error("【设备下发】异常 rod=" + rodId + " sn=" + converter.getConverterSn() + " " + url, e);
            return false;
        }
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
