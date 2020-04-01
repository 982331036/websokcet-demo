package com.lq.websocketdemo.service;

/**
 * @author 123456
 */
public interface IPushMsgService {

    /**
     * 给指定用户发送消息
     * @param userId
     * @param msg
     */
    void pushMsg(String userId,String msg);

    /**
     * 给所有用户发送消息
     * @param msg
     */
    void pushMsg(String msg);

}
