package com.inteink.modules.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inteink.modules.biz.mapper.BizConverterMapper;
import com.inteink.modules.biz.model.entity.BizConverter;
import com.inteink.modules.biz.model.enums.RodStateEnum;
import com.inteink.modules.biz.model.vo.DeviceInfoVO;
import com.inteink.modules.biz.service.DeviceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceQueryServiceImpl implements DeviceQueryService {

    private final BizConverterMapper converterMapper;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2)).build();

    @Override
    public DeviceInfoVO probe(Long converterId) {
        BizConverter converter = converterId == null ? null : converterMapper.selectById(converterId);
        if (converter == null || !converter.isValid()) {
            return null;
        }
        DeviceInfoVO vo = new DeviceInfoVO();
        vo.setConverterId(converter.getConverterId());
        vo.setSn(converter.getConverterSn());
        vo.setIp(converter.getConverterIp());
        vo.setPort(converter.getConverterPort());
        Integer state = readState(converter);
        vo.setOnline(state != null);
        vo.setState(state);
        if (state != null) {
            vo.setStateDesc(RodStateEnum.descOf(state));
        }
        return vo;
    }

    private Integer readState(BizConverter converter) {
        String url = "http://" + converter.getConverterIp() + ":" + converter.getConverterPort() + "/state";
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                return null;
            }
            JSONObject o = JSON.parseObject(resp.body());
            return o == null ? null : o.getInteger("state");
        } catch (Exception e) {
            log.warn("【设备探测】不可达 sn={} {}", converter.getConverterSn(), url);
            return null;
        }
    }
}
