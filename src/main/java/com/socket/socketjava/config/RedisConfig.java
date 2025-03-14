package com.socket.socketjava.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate以使用自定义序列化器
     *
     * @param factory Redis连接工厂
     * @return 配置好的RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 创建ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置ObjectMapper的可见性，以便它可以访问对象的所有属性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 激活默认类型检测，允许处理子类
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        // 禁用时间戳日期写法，以ISO 8601格式写日期
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 注册JavaTimeModule以支持Java 8日期和时间API
        objectMapper.registerModule(new JavaTimeModule());

        // 创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 设置序列化器
        // 使用StringRedisSerializer序列化键，以字符串形式存储键
        template.setKeySerializer(new StringRedisSerializer());
        // 使用自定义的Jackson2JsonRedisSerializer序列化值，以JSON形式存储值
        template.setValueSerializer(serializer);
        // 使用StringRedisSerializer序列化哈希表的键
        template.setHashKeySerializer(new StringRedisSerializer());
        // 使用自定义的Jackson2JsonRedisSerializer序列化哈希表的值
        template.setHashValueSerializer(serializer);

        // 初始化RedisTemplate
        template.afterPropertiesSet();
        return template;
    }
}
