// 定義此類別所屬的套件路徑，用於組織會員相關的功能模組
package com.smashspot.member.model;

// 引入所需的 Spring Framework 相關類別，用於依賴注入和 Redis 操作
import org.springframework.beans.factory.annotation.Autowired;
// 引入 Redis 操作的核心類別，專門用於處理字串類型的資料
import org.springframework.data.redis.core.StringRedisTemplate;
// 標註這是一個 Spring 服務元件
import org.springframework.stereotype.Service;

// 引入 Jackson 函式庫的物件映射器，用於 Java 物件與 JSON 字串的轉換
import com.fasterxml.jackson.databind.ObjectMapper;

// 引入 Java 內建的 UUID 類別，用於生成唯一識別碼
import java.util.UUID;
// 引入時間單位相關的類別，用於設定 Redis 快取的過期時間
import java.util.concurrent.TimeUnit;

// 使用 @Service 註解標示這是一個 Spring 服務類別，會被 Spring 容器管理
@Service
public class RedisService {
    
    // 使用 @Autowired 進行依賴注入，自動注入 StringRedisTemplate 實例
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 定義 Redis key 的前綴常數，用於區分不同用途的資料
    // 用於存放驗證相關的資料
    private static final String VERIFY_PREFIX = "member:verify:";
    // 用於存放臨時會員資料
    private static final String MEMBER_TEMP_PREFIX = "member:temp:";
    // 定義資料的預設過期時間為 30 分鐘
    private static final long EXPIRE_MINUTES = 1;
    // 添加密碼重設相關的 Redis key 前綴
    private static final String PASSWORD_RESET_PREFIX = "password:reset:";
    
    /**
     * 生成驗證 token 並存儲臨時會員資料的方法
     * @param memberVO 要暫存的會員資料物件
     * @return 回傳生成的驗證 token
     */
    public String generateVerificationToken(MemberVO memberVO) {
        try {
            // 使用 UUID 生成一個唯一的 token 字串
            String token = UUID.randomUUID().toString();
            
            // 創建 ObjectMapper 實例，用於將會員物件轉換為 JSON 字串
            ObjectMapper mapper = new ObjectMapper();
            // 將會員物件序列化為 JSON 字串
            String memberJson = mapper.writeValueAsString(memberVO);
            
            // 將會員資料存入 Redis，並設定過期時間
            redisTemplate.opsForValue().set(
                MEMBER_TEMP_PREFIX + token,  // key 為前綴加上 token
                memberJson,                  // value 為序列化後的會員資料
                EXPIRE_MINUTES,             // 設定過期時間
                TimeUnit.MINUTES            // 指定時間單位為分鐘
            );
            
            // 同時儲存 token 與 email 的對應關係
            redisTemplate.opsForValue().set(
                VERIFY_PREFIX + token,      // 驗證用的 key
                memberVO.getEmail(),        // 存入會員的 email
                EXPIRE_MINUTES,             // 設定相同的過期時間
                TimeUnit.MINUTES            // 時間單位為分鐘
            );
            
            return token;  // 回傳生成的 token
        } catch (Exception e) {
            // 如果過程中發生錯誤，拋出執行時期異常
            throw new RuntimeException("生成驗證 token 失敗", e);
        }
    }
    
    /**
     * 根據 token 取得暫存的會員資料
     * @param token 驗證用的 token
     * @return 如果找到則返回會員資料物件，否則返回 null
     */
    public MemberVO getTemporaryMember(String token) {
        try {
            // 從 Redis 中取得暫存的會員資料 JSON 字串
            String memberJson = redisTemplate.opsForValue().get(MEMBER_TEMP_PREFIX + token);
            if (memberJson != null) {
                // 如果找到資料，則將 JSON 轉換回會員物件
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(memberJson, MemberVO.class);
            }
            return null;  // 如果找不到資料則返回 null
        } catch (Exception e) {
            // 發生錯誤時拋出執行時期異常
            throw new RuntimeException("取得暫存會員資料失敗", e);
        }
    }
    
    /**
     * 清除暫存的會員資料和驗證信息
     * @param token 要清除的 token
     */
    public void clearTemporaryData(String token) {
        // 刪除會員暫存資料
        redisTemplate.delete(MEMBER_TEMP_PREFIX + token);
        // 刪除驗證信息
        redisTemplate.delete(VERIFY_PREFIX + token);
    }
    
    /**
     * 產生完整的驗證連結
     * @param token 驗證用的 token
     * @return 完整的驗證 URL
     */
    public String getVerificationLink(String token) {
        // 組合完整的驗證 URL
        return "http://localhost:8080/member/verify?token=" + token;
    }
    
    /**
     * 檢查 token 是否有效
     * @param token 要檢查的 token
     * @return 如果 token 有效則返回 true，否則返回 false
     */
    public boolean isTokenValid(String token) {
        // 檢查驗證信息和會員暫存資料是否都存在
        return redisTemplate.hasKey(VERIFY_PREFIX + token) &&
               redisTemplate.hasKey(MEMBER_TEMP_PREFIX + token);
    }    
    
    public String generatePasswordResetToken(MemberVO member) {
        try {
            // 生成唯一的 token
            String token = UUID.randomUUID().toString();
            
            // 將會員資料轉換為 JSON
            ObjectMapper mapper = new ObjectMapper();
            String memberJson = mapper.writeValueAsString(member);
            
            // 存儲到 Redis，設定 30 分鐘過期
            redisTemplate.opsForValue().set(
                PASSWORD_RESET_PREFIX + token,
                memberJson,
                30,
                TimeUnit.MINUTES
            );
            
            return token;
        } catch (Exception e) {
            throw new RuntimeException("生成密碼重設 token 失敗", e);
        }
    }

    public String getPasswordResetLink(String token) {
        // 返回密碼重設頁面的 URL
        return "http://localhost:8080/member/reset-password?token=" + token;
    }

    public boolean isPasswordResetTokenValid(String token) {
        // 檢查 token 是否存在且未過期
        return redisTemplate.hasKey(PASSWORD_RESET_PREFIX + token);
    }

    public MemberVO getMemberFromPasswordResetToken(String token) {
        try {
            // 從 Redis 獲取會員資料
            String memberJson = redisTemplate.opsForValue().get(PASSWORD_RESET_PREFIX + token);
            if (memberJson != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(memberJson, MemberVO.class);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("獲取會員資料失敗", e);
        }
    }

    public void clearPasswordResetToken(String token) {
        // 清除 Redis 中的 token
        redisTemplate.delete(PASSWORD_RESET_PREFIX + token);
    }

}
