package com.example.onlinestore.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos服务注册配置类
 * 
 * 该配置类用于控制Nacos服务注册的启用/禁用。
 * 通过配置 spring.cloud.nacos.enabled 属性来控制：
 * - 设置为 true 时启用 Nacos 服务注册（默认值）
 * - 设置为 false 时禁用 Nacos 服务注册
 * 
 * 示例配置：
 * spring:
 *   cloud:
 *     nacos:
 *       enabled: true
 *       discovery:
 *         server-addr: localhost:8848
 *         namespace: your-namespace
 */
@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty(name = "spring.cloud.nacos.enabled", havingValue = "true", matchIfMissing = true)
public class NacosConfig {
    // 这个类用于控制Nacos服务注册的启用/禁用
} 