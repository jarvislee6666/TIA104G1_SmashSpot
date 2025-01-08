//package com.smashspot.websocketchat.chat;
//222
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
////import redis.clients.jedis.UnifiedJedis;
//
//public class ChatMessageRepository {
//
//	
//	private static ObjectMapper objectMapper = new ObjectMapper();
//	private static JedisPool pool = JedisPoolUtil.getJedisPool();
//
////	public void save(ChatMessage chatMessage) {
////		try (UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");) {
////			String valueAsString = objectMapper.writeValueAsString(chatMessage);
////			jedis.set(STORE_KEY, valueAsString);
////			String value = jedis.get(STORE_KEY);
////			System.out.println("Value from Redis: " + value);
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////
////	}
//	public void save(ChatMessage chatMessage) {
//		String STORE_KEY = "chat:" + chatMessage.getChatId();
//		try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource();) {			
//			String valueAsString = objectMapper.writeValueAsString(chatMessage);
//			jedis.set(STORE_KEY, valueAsString);
//			String value = jedis.get(STORE_KEY);
//			System.out.println("Value from Redis: " + value);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public List<ChatMessage> findByChatId(String chatId) {
//		 List<ChatMessage> chatMessages = new ArrayList<>();
//	        String key = "chat:" + chatId;
//
//	        try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource()) {
//	            // 遍歷所有訊息
//	            List<String> messagesJson = jedis.lrange(key, 0, -1);
////	            ObjectMapper objectMapper = new ObjectMapper();
//	            for (String json : messagesJson) {
//	            	try {
//	                    // 直接使用 ObjectMapper 將 JSON 字符串解析為 ChatMessage 對象
//	                    ChatMessage message = objectMapper.readValue(json, ChatMessage.class);
//	                    chatMessages.add(message);
//	                } catch (Exception e) {
//	                    // 錯誤處理，例如記錄日誌或拋出異常
//	                    System.err.println("Error parsing JSON: " + e.getMessage());
//	                }
//	            }
//	        }
//	        return chatMessages;
//	}
//}