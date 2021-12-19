package com.iss.rabbit.common.serializer.impl;

import com.iss.rabbit.api.Message;
import com.iss.rabbit.common.serializer.Serializer;
import com.iss.rabbit.common.serializer.SerializerFactory;

/**
 * 序列化工厂实现类
 */
public class JacksonSerializerFactory implements SerializerFactory {

    /**
     * 创建一个单例模式（饿汉式）
     */
    public static final JacksonSerializerFactory INSTANCE = new JacksonSerializerFactory();

    private JacksonSerializerFactory() {
    }

    /**
     * 序列化成指定类型（我们自定义的消息类型）
     *
     * @return
     */
    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }
}
