package com.smashspot.websocketchat.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smashspot.member.model.MemberVO;
import redis.clients.jedis.Jedis;

import java.util.Date;

public class ChatMessageServiceTest {

    public static void main(String[] args) {
        // 初始化 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // 模擬創建 MemberVO
        MemberVO sender = new MemberVO();
        sender.setMemid(101);
        sender.setName("John Doe");
        sender.setEmail("johndoe@example.com");

        // 創建 ChatMessage
        ChatMessage chatMessage = ChatMessage.builder()
                .id("msg123")
                .chatId("chat101")
                .sender(sender)
                .senderName(sender.getName())
                .recipientId("Adm")
                .content("Hello, Redis!")
                .timestamp(new Date())
                .read(false)
                .build();

        // 將 ChatMessage 存入 Redis
        try (Jedis jedis = JedisPoolUtil.getJedisPool().getResource()) {
            // 將 ChatMessage 序列化為 JSON
            String chatMessageJson = objectMapper.writeValueAsString(chatMessage);

            // 使用 Redis 列表 (rpush) 儲存訊息
            String key = "chat:" + chatMessage.getChatId();
            jedis.rpush(key, chatMessageJson);

            System.out.println("Message successfully saved to Redis:");
            System.out.println(chatMessageJson);

            // 從 Redis 中讀取訊息列表
            System.out.println("\nMessages in chat:");
            jedis.lrange(key, 0, -1).forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
