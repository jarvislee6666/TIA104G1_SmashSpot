package com.smashspot.websocketchat.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.smashspot.websocketchat.chatroom.Chatroom;

@Configuration
public class RedisConfig2 {
	
	@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 創建 Redis 連線工廠 (預設連接到 localhost:6379)
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean("redisTemplate2")
    //Object=Chatroom
    public RedisTemplate<String, Chatroom> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Chatroom> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}