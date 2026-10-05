package com.inteink.modules.qyweixin.service;

public interface MessageService {
    /**
     * 验证URL有效性
     * @param msg_signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    String verifyUrl(String msg_signature, String timestamp, String nonce, String echostr);
}
