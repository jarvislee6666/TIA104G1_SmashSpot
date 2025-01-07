// 定義套件路徑，表明此類別屬於會員相關的功能模組
package com.smashspot.member.model;

// 引入必要的Spring Framework和Java工具類別
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// 使用@Service註解將此類別標記為Spring的服務層元件
// Spring會自動掃描並管理這個類別的生命週期
@Service
public class RedisService {
    
    // 注入Spring Data Redis提供的StringRedisTemplate
    // 這是一個線程安全的模板類別，專門用於操作Redis中的字符串類型數據
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 定義Redis中存儲驗證相關數據的鍵值前綴
    // 使用前綴可以讓相關的鍵值形成命名空間，便於管理和避免衝突
    private static final String VERIFY_PREFIX = "member:verify:";
    
    // 定義驗證連結的有效期限（分鐘）
    // 這是一個安全性設置，確保驗證連結不會永久有效
    private static final long EXPIRE_MINUTES = 30;
    
    /**
     * 生成驗證連結的方法
     * 這個方法完成三個主要任務：
     * 1. 生成唯一的token
     * 2. 將token與email的對應關係存入Redis
     * 3. 組裝並返回完整的驗證URL
     * 
     * @param email 需要驗證的用戶電子郵件地址
     * @return 包含驗證token的完整URL
     */
    public String generateVerificationLink(String email) {
        // 使用UUID生成一個全域唯一的token
        // UUID可以保證在分散式系統中的唯一性，降低碰撞的可能性
        String token = UUID.randomUUID().toString();
        
        // 將token和email的對應關係存入Redis
        // 使用Redis的key-value結構存儲，並設置過期時間
        redisTemplate.opsForValue().set(
            VERIFY_PREFIX + token,  // 鍵：前綴+token
            email,                  // 值：用戶的email
            EXPIRE_MINUTES,         // 過期時間長度
            TimeUnit.MINUTES        // 時間單位：分鐘
        );
        
        // 組裝並返回完整的驗證URL
        // 這個URL會通過郵件發送給用戶
        // TODO: 在實際部署時需要修改域名為正式環境的域名
        return "http://localhost:8080/member/verify?token=" + token;
    }
    
    /**
     * 驗證token的有效性並獲取對應的email
     * 這個方法實現了驗證的核心邏輯：
     * 1. 檢查token是否存在
     * 2. 如果存在，取出對應的email
     * 3. 刪除已使用的token（一次性使用）
     * 
     * @param token 要驗證的token字符串
     * @return 如果token有效則返回對應的email，否則返回null
     */
    public String verifyToken(String token) {
        // 構建完整的Redis鍵名
        String key = VERIFY_PREFIX + token;
        
        // 從Redis中獲取存儲的email
        String email = redisTemplate.opsForValue().get(key);
        
        // 如果找到對應的email（token有效）
        if (email != null) {
            // 立即刪除這個token，確保它不能被重複使用
            // 這是一個重要的安全措施
            redisTemplate.delete(key);
        }
        
        // 返回email（如果token無效則為null）
        return email;
    }
    
    /**
     * 檢查token是否存在於Redis中
     * 這個方法可以用來快速檢查token的有效性，而不需要實際獲取email
     * 
     * @param token 要檢查的token
     * @return 如果token存在於Redis中返回true，否則返回false
     */
    public boolean isTokenValid(String token) {
        // 使用Redis的hasKey方法檢查鍵是否存在
        return redisTemplate.hasKey(VERIFY_PREFIX + token);
    }
}