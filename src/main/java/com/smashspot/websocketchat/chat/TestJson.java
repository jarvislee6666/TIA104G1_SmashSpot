//package com.smashspot.websocketchat.chat;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.UnifiedJedis;
//
//public class TestJson {
//	
//	private static ObjectMapper objectMapper = new ObjectMapper();
//
//	public static void main(String[] args) {
//
//		ChatMessage chatMessage = new ChatMessage();
//		String STORE_KEY = "chat:" + chatMessage.getChatId();
//		chatMessage.setId("test-id2");
//		chatMessage.setChatId("test-chat-id2");
//		chatMessage.setSenderId("test-sender-id2");
//		chatMessage.setRecipientId("test-recipient-id");
//		chatMessage.setContent("Hello, this is a test message!");
//		chatMessage.setTimestamp(new Date());
//
//		try (UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");) {
//			String valueAsString = objectMapper.writeValueAsString(chatMessage);
//			jedis.set(STORE_KEY, valueAsString);
//			String value = jedis.get(STORE_KEY);
//			System.out.println("Value from Redis: " + value);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//        // 模擬從 Redis 中獲取的 JSON 列表
//        List<String> messagesJson = List.of(
//                "{\"senderId\":\"Alice\", \"content\":\"Hello\", \"timestamp\":1677891234567}",
//                "{\"senderId\":\"Bob\", \"content\":\"Hi\", \"timestamp\":1677891234570}",
//                "{\"senderId\":\"Charlie\", \"content\":\"Invalid JSON\"" // 錯誤的 JSON
//        );
//
//        List<ChatMessage> chatMessages = new ArrayList<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        for (String json : messagesJson) {
//            try {
//                ChatMessage message = objectMapper.readValue(json, ChatMessage.class);
//                chatMessages.add(message);
//            } catch (Exception e) {
//                System.err.println("Error parsing JSON: " + e.getMessage());
//            }
//        }
//
//        // 輸出解析成功的訊息
//        chatMessages.forEach(message -> {
//            System.out.println("Sender: " + message.getSenderId());
//            System.out.println("Message: " + message.getContent());
//            System.out.println("Timestamp: " + message.getTimestamp());
//        });
//	}
//}
