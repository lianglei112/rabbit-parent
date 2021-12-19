package com.iss.rabbit.api;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义消息的实体封装类
 */
@Data
public class Message implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -596315917304735553L;

    /**
     * 消息的唯一id
     */
    private String messageId;
    /**
     * 消息的主题
     */
    private String topic;
    /**
     * 消息的路由规则
     */
    private String routingKey = "";
    /**
     * 设置相关的属性（消息的附加属性）
     */
    private Map<String, Object> attributes = new HashMap<String, Object>();
    /**
     * 延迟消息的参数配置
     */
    private int delayMills;
    /**
     * 设置消息的类型（默认为 confirm 消息类型）
     */
    private String messageType = MessageType.CONFIRM;

    public Message() {
    }

    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, int delayMills) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
    }

    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, int delayMills, String messageType) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
        this.messageType = messageType;
    }
}
