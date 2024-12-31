package com.smashspot.member.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(
            "verification:" + email,
            code,
            30,
            TimeUnit.MINUTES
        );
    }
    
    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get("verification:" + email);
    }
    
    public void deleteVerificationCode(String email) {
        redisTemplate.delete("verification:" + email);
    }
}