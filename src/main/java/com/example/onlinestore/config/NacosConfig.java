package com.example.onlinestore.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty(name = "spring.cloud.nacos.enabled", havingValue = "true", matchIfMissing = true)
public class NacosConfig {
    // 这个类用于控制Nacos服务注册的启用/禁用
} 