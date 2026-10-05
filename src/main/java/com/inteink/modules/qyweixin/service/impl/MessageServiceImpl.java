package com.inteink.modules.qyweixin.service.impl;

import com.inteink.modules.qyweixin.service.MessageService;
import com.inteink.modules.qyweixin.utils.aes.AesException;
import com.inteink.modules.qyweixin.utils.aes.WXBizJsonMsgCrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    private static final String token = "<REDACTED>";
    private static final String encodingAesKey = "<REDACTED>";
    private static final String corpid = "<REDACTED>";

    /**
     * 验证URL有效性
     * @param msg_signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @Override
    public String verifyUrl(String msg_signature, String timestamp, String nonce, String echostr) {

        log.info("echostr:{}",echostr);
        //1.对收到的请求做Urldecode处理
        echostr = URLDecoder.decode(echostr);
        log.info("msg_signature:{}",msg_signature);
        log.info("timestamp:{}",timestamp);
        log.info("nonce:{}",nonce);
        log.info("echostr:{}",echostr);
        //2.通过参数msg_signature对请求进行校验，确认调用者的合法性。
        //3.解密echostr参数得到消息内容(即msg字段)
        WXBizJsonMsgCrypt msgCrypt = null;
        String msg = null;
        try {
            msgCrypt = new WXBizJsonMsgCrypt(token, encodingAesKey, corpid);
            msg = msgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
        } catch (AesException e) {
            e.printStackTrace();
        }

        //4.在1秒内原样返回明文消息内容(不能加引号，不能带bom头，不能带换行符)
        return msg;
    }
}
