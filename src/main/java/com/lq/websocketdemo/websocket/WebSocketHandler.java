package com.lq.websocketdemo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



/**
 * @author lq
 * @date 2020/4/1 10:15
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 一旦连接，第一个被执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded 被调用{}",ctx.channel().id().asLongText());
        NettyConfig.getChannelGroup().add(ctx.channel());
    }

    /**
     * 读取数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("服务端收到消息：{}",msg.text());
        /**
         * 获取用户id，关联channel
         */
        JSONObject map = JSON.parseObject(msg.text());
        String uid = map.get("uid").toString();
        NettyConfig.getUserChannelMap().put(uid,ctx.channel());

        //将用户id作为自定义属性加入到channel 中，方便随时channel中获取用户id
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        ctx.channel().attr(key).setIfAbsent(uid);

        //回复消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器连接成功！"));


    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved被调用{}",ctx.channel().id().asLongText());
        //删除通道
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("exceptionCaught被调用，异常：{}",cause.getMessage());
        //删除通道
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
        ctx.close();
    }

    /**
     * 删除用户与channel 对应关系
     * @param ctx
     */
    private void removeUserId(ChannelHandlerContext ctx){
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        NettyConfig.getUserChannelMap().remove(userId);
    }
}
