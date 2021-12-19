package com.iss.rabbit.producer.broker;

import com.iss.rabbit.api.Message;
import com.iss.rabbit.api.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
