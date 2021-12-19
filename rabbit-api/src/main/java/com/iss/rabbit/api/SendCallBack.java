package com.iss.rabbit.api;

/**
 * 回调函数处理（需要确认消息回调处理）
 */
public interface SendCallBack {

    /**
     * 消息发送成功时的回调
     */
    void onSuccess();

    /**
     * 消息发送失败时的回调
     */
    void onFailure();
}
