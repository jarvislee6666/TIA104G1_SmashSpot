package com.smashspot.websocketchat.chatroom;

import com.smashspot.member.model.MemberVO;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class ChatroomServiceTest2 {

    public static void main(String[] args) {
        // 1. 初始化 Spring 上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RedisConfig2.class);
        RedisTemplate<String, Chatroom> redisTemplate = context.getBean("redisTemplate2", RedisTemplate.class);

        // 2. 建立 MemberVO
        MemberVO member = new MemberVO();
        member.setMemid(1);
        member.setName("TestUser");
        member.setEmail("testuser@example.com");

        // 3. 建立 Chatroom
        Chatroom chatroom = Chatroom.builder()
                .id("1")
                .chatId("TestUser_Adm")
                .sender(member)
                .recipientId("Adm")
                .lastMessage("Hello, Redis!")
                .unreadCount(1)
                .build();

        // 4. 存入 Redis
        redisTemplate.opsForValue().set(chatroom.getChatId(), chatroom);

        // 5. 從 Redis 讀取
        Chatroom retrievedChatroom = redisTemplate.opsForValue().get(chatroom.getChatId());

        // 6. 驗證結果
        if (retrievedChatroom != null) {
            System.out.println("Chatroom successfully stored and retrieved from Redis.");
            System.out.println("Retrieved Chatroom: " + retrievedChatroom);
        } else {
            System.out.println("Failed to retrieve Chatroom from Redis.");
        }

        // 7. 關閉 Spring 上下文
        context.close();
    }
}
