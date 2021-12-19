package com.iss.rabbit.common.serializer;

/**
 * 序列化和反序列化的接口
 */
public interface Serializer {

    /**
     * 序列化：传入一个对象序列化成为一个字节数组
     *
     * @param data
     * @return
     */
    byte[] serializeRaw(Object data);

    /**
     * 序列化：传入一个对象序列化成为一个字符串
     *
     * @param data
     * @return
     */
    String serialize(Object data);

    /**
     * 反序列化：传入一个需要被反序列化的对象转换为我们指定的类型
     *
     * @param content
     * @param <T>
     * @return
     */
    <T> T deserialize(String content);

    /**
     * 反序列化：传入一个需要被反序列化的Object对象序列化成为一个我们自己指定的类型
     *
     * @param content
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] content);
}
