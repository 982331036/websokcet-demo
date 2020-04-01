package com.lq.websocketdemo.controller;

import com.lq.websocketdemo.service.IPushMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lq
 * @date 2020/4/1 11:22
 */
@RestController
public class PushMsgController {

    @Autowired
    private IPushMsgService pushMsgService;

    @PostMapping("/pushUser")
    public String pushUser(String userId,String msg){
        pushMsgService.pushMsg(userId, msg);
        return "消息发送成功："+msg;
    }

    @PostMapping("/pushAll")
    public String pushAll(String msg){
        pushMsgService.pushMsg(msg);
        return "消息发送成功："+msg;
    }

}
