package com.iss.rabbit.api;

import com.iss.rabbit.api.exception.MessageRunTimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息的建造者模式
 */
public class MessageBuilder {

    private String messageId;
    private String topic;
    private String routingKey = "";
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private int delayMills;
    private String messageType = MessageType.CONFIRM;

    private MessageBuilder(){
    }

    public static MessageBuilder create(){
        return new MessageBuilder();
    }

    public MessageBuilder withMessageId(String messageId){
        this.messageId = messageId;
        return this;
    }

    public MessageBuilder withTopic(String topic){
        this.topic = topic;
        return this;
    }

    public MessageBuilder withRoutingKey(String routingKey){
        this.topic = routingKey;
        return this;
    }

    public MessageBuilder withAttributes(Map<String,Object> attributes){
        this.attributes = attributes;
        return this;
    }

    public MessageBuilder withAttribute(String key,Object value){
        this.attributes.put(key,value);
        return this;
    }

    public MessageBuilder withDelayMills(Integer delayMills){
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder whitMessageType(String messageType){
        this.messageType = messageType;
        return this;
    }

    public Message build(){
        //1、check messageId
        if(messageId == null){
            messageId = UUID.randomUUID().toString().trim();
        }
        //2、topic is null
        if(topic == null){
            throw new MessageRunTimeException("this topic is null！");
        }
        Message message = new Message(messageId,topic,routingKey,attributes,delayMills,messageType);
        return message;
    }
}
