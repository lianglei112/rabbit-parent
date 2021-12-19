package com.iss.rabbit.api;

/**
 * 消费者（监听消息）
 */
public interface MessageListener {

    /**
     * 消费者处理消息
     */
    void onMessage(Message message);
}
