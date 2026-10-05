package com.inteink.main;


import lombok.extern.slf4j.Slf4j;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WsTest {
    public static void main(String[] args) throws Exception{
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("origin", "https://dihu.inteink.com");
        WSClient client = new WSClient(new URI("wss://dihu.inteink.com:8088/ws"),httpHeaders);
        client.connect();
        while (!client.getReadyState().equals(ReadyState.OPEN)) {
            log.info("state:{}",client.getReadyState());
            log.info("连接中...");
            Thread.sleep(1000);
        }
        log.info("连接成功:{}",client);

        client.send("test");
    }
}
