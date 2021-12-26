package com.iss.rabbit.common.convert;

import org.assertj.core.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * 序列化与反序列化的装饰者模式
 */
public class RabbitMessageConverter implements MessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMessageConverter.class);

    private GenericMessageConverter delegate;

    /**
     * 设置消息的过期时间（设置过期时间为一天）
     */
    private final String delaulExpiration = String.valueOf(24 * 60 * 60 * 1000);

    public RabbitMessageConverter(GenericMessageConverter genericMessageConverter) {
        Preconditions.checkNotNull(genericMessageConverter);
        this.delegate = genericMessageConverter;
    }

    /**
     * 序列化的过程
     *
     * @param object
     * @param messageProperties
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        messageProperties.setExpiration(delaulExpiration);
//        messageProperties.setDelay();   可以设置消息的延迟
//        messageProperties.setContentEncoding();    可以设置字符集编码
//        messageProperties.setPriority();        可以设置消息发送的优先级
//        messageProperties.setMessageId();      可以设置消息的唯一id
        return this.delegate.toMessage(object, messageProperties);
    }

    /**
     * 反序列化的过程
     *
     * @param message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        com.iss.rabbit.api.Message msg = (com.iss.rabbit.api.Message) this.delegate.fromMessage(message);
        return msg;
    }
}
