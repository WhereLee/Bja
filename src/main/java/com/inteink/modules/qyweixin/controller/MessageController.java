package com.inteink.modules.qyweixin.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.annotation.SysLog;
import com.inteink.modules.qyweixin.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信 消息处理
 */
@Api(tags = "企业微信",position = 990)
@RestController
@RequestMapping("/qyweixin")
public class MessageController {
    @Autowired
    private MessageService messageService;

    /**
     * 验证URL有效性
     */
    @ApiOperation(value = "验证URL有效性",notes = "验证URL有效性-企业微信发起")
    @ApiOperationSupport(order = 30)
    @SysLog(module = "其他模块",func = "企业微信",value = "验证URL有效性")
    @GetMapping("/url/verify")
    public String verifyUrl(String msg_signature, String timestamp, String nonce, String echostr){
        return messageService.verifyUrl(msg_signature, timestamp, nonce, echostr);
    }
}
