package com.iss.rabbit.api;

import com.iss.rabbit.api.exception.MessageRunTimeException;

import java.util.List;

/**
 * 生产者（发送消息）
 */
public interface MessageProducer {

    /**
     * send 消息的发送  附带SendCallBack回调执行响应的业务逻辑处理
     *
     * @param message
     * @param sendCallBack
     * @throws MessageRunTimeException
     */
    void send(Message message, SendCallBack sendCallBack) throws MessageRunTimeException;

    /**
     * message 消息的发送
     *
     * @param message
     * @throws MessageRunTimeException
     */
    void send(Message message) throws MessageRunTimeException;

    /**
     * message 消息的批量发送
     *
     * @param messages
     * @throws MessageRunTimeException
     */
    void send(List<Message> messages) throws MessageRunTimeException;
}
