package com.lq.websocketdemo.service.impl;

import com.lq.websocketdemo.service.IPushMsgService;
import com.lq.websocketdemo.websocket.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

/**
 * @author lq
 * @date 2020/4/1 11:20
 */
@Service
public class PushMsgServiceImpl implements IPushMsgService {
    @Override
    public void pushMsg(String userId, String msg) {
        Channel channel = NettyConfig.getUserChannelMap().get(userId);
        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    @Override
    public void pushMsg(String msg) {
        ChannelGroup group = NettyConfig.getChannelGroup();
        String name = group.name();
        System.out.println("空间大小："+group.size()+",名字："+name);
        group.writeAndFlush(new TextWebSocketFrame(msg));
    }
}
