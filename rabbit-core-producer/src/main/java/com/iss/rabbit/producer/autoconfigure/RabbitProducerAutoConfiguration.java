package com.iss.rabbit.producer.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitProducerAutoConfiguration：做自动装配的类（Springboot加载时，
 * 会帮我们把这个类里边的内容做自动装配）
 */
@Configuration
@ComponentScan({"com.iss.rabbit.producer.*"})
public class RabbitProducerAutoConfiguration {


}
