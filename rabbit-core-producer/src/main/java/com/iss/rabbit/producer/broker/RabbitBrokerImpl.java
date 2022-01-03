package com.iss.rabbit.producer.broker;

import com.iss.rabbit.api.Message;
import com.iss.rabbit.api.MessageType;
import com.iss.rabbit.producer.constant.BrokerMessageConst;
import com.iss.rabbit.producer.constant.BrokerMessageStatus;
import com.iss.rabbit.producer.entity.BrokerMessage;
import com.iss.rabbit.producer.service.MessageStoreService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataUnit;

import java.util.Date;

/**
 * 具体发送不同类型消息的实现类
 */
@Component
public class RabbitBrokerImpl implements RabbitBroker {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitBroker.class);

    /**
     * RabbitTemplate的池化封装自动装配，根据不同的交换器类型发送不同的消息
     */
    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    /**
     * 发送可靠性的详细需要修改表中对应的状态
     */
    @Autowired
    private MessageStoreService messageStoreService;

    /**
     * 发送迅速消息
     *
     * @param message
     */
    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    /**
     * 发送确认消息
     *
     * @param message
     */
    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    /**
     * 发送可靠性消息
     *
     * @param message
     */
    @Override
    public void reliantSend(Message message) {
        message.setMessageType(MessageType.RELIANT);
        //1. 首先消息发送之前消息日志落地日志数据库
        Date now = new Date();
        BrokerMessage brokerMessage = new BrokerMessage();
        brokerMessage.setMessageId(message.getMessageId());
        brokerMessage.setMessage(message);
        //tryCount 在最开始发送的时候不需要进行设置
        //设置消息重新发送时间间隔
        brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
        brokerMessage.setStatus(BrokerMessageStatus.SENDING.getStatus());
        brokerMessage.setCreateTime(now);
        brokerMessage.setUpdateTime(now);
        messageStoreService.insert(brokerMessage);
        //2.真正的发送消息逻辑
        sendKernel(message);
    }

    /**
     * 发送批量消息
     */
    @Override
    public void sendMessages() {

    }

    /**
     * 发送消息的核心方法  使用异步线程池进行发送消息
     *
     * @param message
     */
    private void sendKernel(Message message) {
        //使用线程池异步去提交任务
        AsyncBaseQueue.submit((Runnable) () -> {
            //生产者和rabbitmq节点发送消息的唯一标识
            CorrelationData correlationData = new CorrelationData(String.format("%s#%s", message.getMessageId(), System.currentTimeMillis()));
            //交换器类型
            String topic = message.getTopic();
            //路由键
            String routingKey = message.getRoutingKey();
            //根据消息中的topic创建不同的RabbitTemplate模板
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            LOGGER.info("#[RabbitBrokerImpl].[sendKernel]# send to rabbitmq , messageId：{}", message.getMessageId());
        });
    }
}
