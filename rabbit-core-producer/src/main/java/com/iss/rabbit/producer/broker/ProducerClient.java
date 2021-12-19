package com.iss.rabbit.producer.broker;

import com.iss.rabbit.api.Message;
import com.iss.rabbit.api.MessageProducer;
import com.iss.rabbit.api.MessageType;
import com.iss.rabbit.api.SendCallBack;
import com.iss.rabbit.api.exception.MessageRunTimeException;
import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ProducerClient（发送消息的实际实现类）
 */
@Component
public class ProducerClient implements MessageProducer {

    /**
     * 自动装配发送消息的实体类
     */
    @Autowired
    private RabbitBroker rabbitBroker;

    /**
     * 发送单个消息
     *
     * @param message
     * @throws MessageRunTimeException
     */
    @Override
    public void send(Message message) throws MessageRunTimeException {
        //首先去检查我们的交换器的类型是否为空，为空的话则抛出异常
        Preconditions.checkNotNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:   //发送迅速消息
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:  //发送确认消息
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:   //发送可靠性消息
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }

    }

    /**
     * 发送批量消息
     *
     * @param messages
     * @throws MessageRunTimeException
     */
    @Override
    public void send(List<Message> messages) throws MessageRunTimeException {

    }

    /**
     * 带有回调的消息
     *
     * @param message
     * @param sendCallBack
     * @throws MessageRunTimeException
     */
    @Override
    public void send(Message message, SendCallBack sendCallBack) throws MessageRunTimeException {

    }
}
