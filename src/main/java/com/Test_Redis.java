package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class Test_Redis implements CommandLineRunner{
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	@Override
    public void run(String... args) throws Exception {
        try {
            // 測試存儲和讀取
            redisTemplate.opsForValue().set("test", "test redis");
            String value = redisTemplate.opsForValue().get("test");
            System.out.println("Redis測試值: " + value);
            
            // 測試刪除
//            redisTemplate.delete("test");
            
            System.out.println("Redis連接測試成功!");
        } catch (Exception e) {
            System.out.println("Redis連接測試失敗: " + e.getMessage());
        }
    }

}
