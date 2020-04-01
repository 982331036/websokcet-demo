package com.lq.websocketdemo.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lq
 * @date 2020/4/1 9:57
 */
public class NettyConfig {

    /**
     * 定义一个channel组，管理所有channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup("用户管理组",GlobalEventExecutor.INSTANCE);

    /**
     * 存放用户与chanel 的对应的信息，用于给指定用户发送信息
     */
    private static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    public NettyConfig() {
    }

    /**
     * 获取用户channel 组
     * @return
     */
    public static ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    /**
     * 获取用户channel map
     * @return
     */
    public static ConcurrentHashMap<String, Channel> getUserChannelMap() {
        return userChannelMap;
    }
}
