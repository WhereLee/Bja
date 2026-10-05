package com.inteink.main;

import com.inteink.modules.qyweixin.utils.aes.WXBizJsonMsgCrypt;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;

@Slf4j
public class QywxTest {
    public static void main(String[] args) throws Exception{
        String token = "<REDACTED>";
        String encodingAesKey = "<REDACTED>";
        String corpid = "<REDACTED>";

        String msg_signature = "0e524f7800ae7f57325c97c29e0adde42dba6080";
        String timestamp = "1669194908";
        String nonce = "1668392780";
        String echostr = "fFgu1%2BbJnSi49rLex24cjl0ZMN10shSZ%2FN0JIQma0l6l2jpMUB%2Fbou9nYgOg7wmKpppUZqQ1AnhEOsz2oF7vuw%3D%3D";

        //验证URL有效性
        //1.对收到的请求做Urldecode处理
        echostr = URLDecoder.decode(echostr);
        log.info("echostr:{}",echostr);

        //2.通过参数msg_signature对请求进行校验，确认调用者的合法性。
        //3.解密echostr参数得到消息内容(即msg字段)
        WXBizJsonMsgCrypt msgCrypt = new WXBizJsonMsgCrypt(token, encodingAesKey, corpid);
        String msg = msgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
        //2912431344002662367
        log.info("msg:{}",msg);

        //4.在1秒内原样返回明文消息内容(不能加引号，不能带bom头，不能带换行符)

    }
}
