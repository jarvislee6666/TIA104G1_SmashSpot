package com.smashspot.websocketchat.chatroom;

import java.util.Optional;

import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;
import com.smashspot.websocketchat.chat.MockMemberService;

public class ChatroomServiceTest {

    public static void main(String[] args) {
        // 模擬 MemberService 和 ChatroomRepository
        MemberService memberService = new MockMemberService();
        ChatroomRepository chatroomRepository = new MockChatroomRepository();

        // 創建 ChatroomService 實例
        ChatroomService chatroomService = new ChatroomService(chatroomRepository, memberService);

        // 測試數據
        Integer senderId = 1;

        // 測試創建新聊天室
        Optional<String> chatroomId = chatroomService.getChatroomId(senderId, true);

        // 驗證結果
        if (chatroomId.isPresent()) {
            System.out.println("Chatroom created successfully: " + chatroomId.get());
        } else {
            System.out.println("Failed to create chatroom.");
        }

        // 驗證聊天室是否存儲到 Redis
        String senderName = "JohnDoe"; // 模擬的發送者名稱
        Optional<Chatroom> savedChatroom = chatroomRepository.findBySenderNameAndRecipientId(senderName, "Adm");
        if (savedChatroom.isPresent()) {
            System.out.println("Chatroom saved in Redis:");
            System.out.println("ChatID: " + savedChatroom.get().getChatId());
            System.out.println("Sender Name: " + savedChatroom.get().getSenderName());
            System.out.println("Recipient ID: " + savedChatroom.get().getRecipientId());
        } else {
            System.out.println("Chatroom not found in Redis.");
        }
    }
}
