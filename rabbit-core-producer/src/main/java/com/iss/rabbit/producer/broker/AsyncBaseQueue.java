package com.iss.rabbit.producer.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 创建线程池
 */
public class AsyncBaseQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncBaseQueue.class);

    //获取Java虚拟机可用的处理器数量
    private static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    //设置队列的大小
    private static final int QUEUE_SIZE = 10000;

    private static ExecutorService senderAsync = new ThreadPoolExecutor(THREAD_SIZE,
            THREAD_SIZE,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_SIZE),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("rabbitmq_client_async_sender");
                    return t;
                }
            },
            //设置拒绝策略，消息发送失败时会打印以下日志
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    LOGGER.error("async sender is error rejected，runnable：{}，executor：{}", r, executor);
                }
            }
    );

    /**
     * 提交任务的方法
     *
     * @param runnable 表示要提交的任务
     */
    public static void submit(Runnable runnable) {
        senderAsync.submit(runnable);
    }
}
