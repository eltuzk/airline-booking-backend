package com.airlinebooking.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.datasource.data.redis.host}")
    private String host;
    @Value("${spring.datasource.data.redis.port}")
    private Integer port;
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);

    }
    @Bean
    public RedisTemplate<String, String> redisTemplate() {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();//Tạo template kiểu String -> String.
        redisTemplate.setConnectionFactory(redisConnectionFactory());// Gắn connection factory đang có

        StringRedisSerializer serializer = new  StringRedisSerializer(); // Dùng serializer string thống nhất
        redisTemplate.setKeySerializer(serializer); // Serialize Redis key dạng text
        redisTemplate.setValueSerializer(serializer); // Serializer value thường dạng text
        redisTemplate.setHashKeySerializer(serializer); //  Serializer hash field dạng text, ví dụ "101"
        redisTemplate.setHashValueSerializer(serializer); // Serializer hash value dạng text, ví dụ "3"

        redisTemplate.afterPropertiesSet(); // Hoàn tất cấu hình bean

        return  redisTemplate; // Trả bean cho spring dùng

    }

}
