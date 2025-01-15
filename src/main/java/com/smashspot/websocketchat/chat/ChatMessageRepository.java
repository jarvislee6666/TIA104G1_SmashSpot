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
//import redis.clients.jedis.UnifiedJedis;

@Repository 
public class ChatMessageRepository {

	
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static JedisPool pool = JedisPoolUtil.getJedisPool();

	
	public void save(ChatMessage chatMessage) {
	    String STORE_KEY = "chat:" + chatMessage.getChatId();
	    try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource()) {
	        String valueAsString = objectMapper.writeValueAsString(chatMessage);
	        jedis.rpush(STORE_KEY, valueAsString); // 使用 rpush 將訊息追加到列表
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public List<ChatMessage> findByChatId(String chatId) {
	    List<ChatMessage> chatMessages = new ArrayList<>();
	    String key = "chat:" + chatId;

	    try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource()) {
	        // 獲取聊天室所有訊息
	        List<String> messagesJson = jedis.lrange(key, 0, -1);
	        
	        // 解析每筆訊息
	        for (String json : messagesJson) {
	            try {
	                ChatMessage message = objectMapper.readValue(json, ChatMessage.class);
	                chatMessages.add(message);
	            } catch (JsonMappingException e) {
	                // JSON 映射異常處理
	                System.err.println("Error mapping JSON: " + e.getMessage());
	            } catch (JsonProcessingException e) {
	                // JSON 處理異常處理
	                System.err.println("Error processing JSON: " + e.getMessage());
	            }
	        }
	        // 依照時間戳記排序
	        return chatMessages.stream()
	               .sorted(Comparator.comparing(ChatMessage::getTimestamp))
	               .collect(Collectors.toList());
	    }
	}
}