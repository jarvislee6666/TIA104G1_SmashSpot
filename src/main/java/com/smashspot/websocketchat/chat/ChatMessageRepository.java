package com.smashspot.websocketchat.chat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository 
public class ChatMessageRepository {

	// 使用 ObjectMapper 將 ChatMessage 與 JSON 互相轉換
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static JedisPool pool = JedisPoolUtil.getJedisPool();

	
	/**
     * 保存聊天訊息至 Redis
     * @param chatMessage 聊天訊息物件
     */
	public void save(ChatMessage chatMessage) {
		
		// Redis 鍵名：chat:<chatId>
	    String STORE_KEY = "chat:" + chatMessage.getChatId();
	    
	    try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource()) {
	    	// 將 ChatMessage 物件序列化為 JSON 字串
	        String valueAsString = objectMapper.writeValueAsString(chatMessage);
	        jedis.rpush(STORE_KEY, valueAsString); // 使用 rpush 將訊息追加到列表
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	 /**
     * 根據聊天室 ID 獲取所有聊天訊息
     * @param chatId 聊天室 ID
     * @return 聊天訊息列表，按時間排序
     */
	public List<ChatMessage> findByChatId(String chatId) {
	    List<ChatMessage> chatMessages = new ArrayList<>();
	    // Redis 鍵名：chat:<chatId>
	    String key = "chat:" + chatId;

	    try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource()) {
	    	// 從 Redis 列表中獲取所有訊息
	        List<String> messagesJson = jedis.lrange(key, 0, -1);
	        
	        // 將 JSON 字串解析為 ChatMessage 物件
	        for (String json : messagesJson) {
	            try {
	                ChatMessage message = objectMapper.readValue(json, ChatMessage.class);
	                chatMessages.add(message);
	            } catch (JsonMappingException e) {
	                System.err.println("Error mapping JSON: " + e.getMessage());
	            } catch (JsonProcessingException e) {
	                System.err.println("Error processing JSON: " + e.getMessage());
	            }
	        }
	        // 依照時間戳記排序
	        return chatMessages.stream()
	               .sorted(Comparator.comparing(ChatMessage::getTimestamp))
	               .collect(Collectors.toList());
	    }
	}
	
	
	 /**
     * 更新指定聊天室的所有聊天訊息
     * @param chatId 聊天室 ID
     * @param updatedMessages 更新後的聊天訊息列表
     */
	public void updateMessages(String chatId, List<ChatMessage> updatedMessages) {
		// Redis 鍵名：chat:<chatId>
		String redisKey = "chat:" + chatId;

	    try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource()) {
	        // 清空舊列表
	        jedis.del(redisKey);

	        // 將更新後的消息逐條寫入 Redis
	        for (ChatMessage message : updatedMessages) {
	            String updatedJson = objectMapper.writeValueAsString(message);
	            jedis.rpush(redisKey, updatedJson);
	        }
	    } catch (Exception e) {
	        System.err.println("Error updating messages for chatId: " + chatId);
	        e.printStackTrace();
	    }
	}

}