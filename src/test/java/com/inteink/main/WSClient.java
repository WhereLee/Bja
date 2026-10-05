package com.inteink.main;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

@Slf4j
public class WSClient extends WebSocketClient {

    public WSClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("onOpen:{}",handshakedata);
    }

    @Override
    public void onMessage(String message) {
        log.info("message:{}",message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("code:{},reason:{},remote:{}",code,reason,remote);
    }

    @Override
    public void onError(Exception ex) {
        log.info("onError:{}",ex.getMessage());
    }
}
