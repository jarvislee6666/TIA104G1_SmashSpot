package com.smashspot.websocketchat.chatroom;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatroomRepository {

    private static final String HASH_KEY = "Chatrooms";

    // 在 RedisConfig 中實作，用來支援序列化/反序列化
    private final RedisTemplate<String, Chatroom> redisTemplate;
    // HashOperations<HASH_KEY, generateKey, value>
    private HashOperations<String, String, Chatroom> hashOperations;

    public ChatroomRepository(RedisTemplate<String, Chatroom> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }
    
    /**
     * 獲取所有聊天室
     * @return 聊天室集合
     */
    public Collection<Chatroom> findAll() {
        return hashOperations.values(HASH_KEY);
    }
    
    /**
     * 根據 chatId 查詢聊天室
     * @param chatId 聊天室ID
     * @return Optional<Chatroom>
     */
    // 取得Redis Hash中所有值(即所有Chatroom)，轉換成stream並透過filter過濾條件
    // .findFirst()是一個短路操作，當找到第一個符合條件的元素後，會立即停止遍歷
    public Optional<Chatroom> findByChatId(String chatId) {
        return hashOperations.entries(HASH_KEY).values().stream()
                .filter(chatroom -> chatId.equals(chatroom.getChatId()))
                .findFirst();
    }

    /**
    * 根據 senderName 和 recipientId 查詢聊天室
    * @param senderName 發送者名稱
    * @param recipientId 接收者ID
    * @return Optional<Chatroom>
    */
    public Optional<Chatroom> findBySenderNameAndRecipientId(String senderName, String recipientId) {
        String key = generateKey(senderName, recipientId);
        return Optional.ofNullable(hashOperations.get(HASH_KEY, key));
    }

    /**
     * 儲存 Chatroom 資料到 Redis
     */
    public void save(Chatroom chatroom) {
    	 if (chatroom.getSender() == null || chatroom.getSender().getName() == null) {
    	        throw new IllegalArgumentException("Chatroom sender or senderName is missing.");
    	    }
        String key = generateKey(chatroom.getSenderName(), chatroom.getRecipientId());
        hashOperations.put(HASH_KEY, key, chatroom);
    }

    /**
     * 生成唯一鍵值
     * @param senderName 發送者名稱
     * @param recipientId 接收者 ID
     * @return 唯一鍵值
     */
    private String generateKey(String senderName, String recipientId) {
        return String.format("%s_%s", senderName, recipientId);
    }
    
    /**
     * 刪除聊天室
     * @param senderName 發送者名稱
     * @param recipientId 接收者ID
     */
    public void delete(String senderName, String recipientId) {
        String key = generateKey(senderName, recipientId);
        hashOperations.delete(HASH_KEY, key);
    }
}
