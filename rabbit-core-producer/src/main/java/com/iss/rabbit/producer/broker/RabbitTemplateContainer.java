package com.iss.rabbit.producer.broker;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.iss.rabbit.api.Message;
import com.iss.rabbit.api.MessageType;
import com.iss.rabbit.api.exception.MessageException;
import com.iss.rabbit.api.exception.MessageRunTimeException;
import com.iss.rabbit.common.convert.GenericMessageConverter;
import com.iss.rabbit.common.convert.RabbitMessageConverter;
import com.iss.rabbit.common.serializer.Serializer;
import com.iss.rabbit.common.serializer.SerializerFactory;
import com.iss.rabbit.common.serializer.impl.JacksonSerializerFactory;
import org.assertj.core.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 池化 RabbitTemplate（多生产者模式，提高发送消息的效率）
 * 每一个 topic 对应一个 RabbitTemplate
 * 1、提高发送的效率
 * 2、可以根据不同的需求制定化不同的 RabbitTemplate，比如每一个 topic 都有自己的 routeringKey 规则
 */
@Component
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {

    /**
     * 获取发送日志的消息类
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitTemplateContainer.class);

    /**
     * 为不同类型的交换器创建不同类型的RabbitTemplate。以Topic、RabbitTemplate作为键值对
     */
    private Map<String, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();

    private Splitter splitter = Splitter.on("#");

    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    /**
     * 自动装配连接工厂，用于后边创建RabbitTemplate使用
     */
    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * 通过ConnectionFactory创建一个RabbitTemplate对象
     *
     * @param message
     * @return
     * @throws MessageException
     */
    public RabbitTemplate getTemplate(Message message) throws MessageRunTimeException {
        //校检传过来的消息是否为空
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        //判断是否已经存在RabbitTemplae，已经存在的话直接返回，不存在在创建一个新的
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }
        LOGGER.info("#[RabbitTemplateContainer].[getTemplate]# topic：{} is not exists ，create one ", topic);
        RabbitTemplate newTemplate = new RabbitTemplate(connectionFactory);
        newTemplate.setExchange(topic);
        newTemplate.setRoutingKey(message.getRoutingKey());
        newTemplate.setRetryTemplate(new RetryTemplate());

        //对于message的序列化方式（序列化的操作是为了提高性能）
        //添加序列化反序列化和converter对象
        //通过序列化工厂创建序列化方法操作类
        Serializer serializer = serializerFactory.create();
        //创建序列化和反序列化转换类
        GenericMessageConverter gmc = new GenericMessageConverter(serializer);
        //创建序列化和反序列化装饰类（在对应的方法中可以添加自己的响应逻辑）
        RabbitMessageConverter rmc = new RabbitMessageConverter(gmc);
        //设置此模板的消息转换器
        newTemplate.setMessageConverter(rmc);

        //获取我们消息当中的类型
        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)) {
            newTemplate.setConfirmCallback(this::confirm);
        }
        //不存在在做键值对的关联
        rabbitMap.putIfAbsent(topic, newTemplate);
        return rabbitMap.get(topic);
    }

    /**
     * 确认消息的回调接口（只有确认消息才会调用这个方法）
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //具体的消息应答
        List<String> strings = splitter.splitToList(correlationData.getId());
        String messageId = strings.get(0);
        Long sendTime = Long.parseLong(strings.get(1));
        if (ack) {
            LOGGER.info("send message is OK ， confirm messageId：{} ，snedTime：{}", messageId, sendTime);
        } else {
            LOGGER.error("send message is Fail ，confirm messageId：{} ， sendTime：{} ", messageId, sendTime);
        }

    }
}
