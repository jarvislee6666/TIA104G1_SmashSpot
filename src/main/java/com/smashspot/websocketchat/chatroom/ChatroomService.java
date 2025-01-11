package com.smashspot.websocketchat.chatroom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final MemberService memberService; // 注入 MemberService，用於獲取 MemberVO

    /**
     * 獲取所有聊天室
     * @return 聊天室列表
     */
    public List<Chatroom> getAllChatrooms() {
        Collection<Chatroom> chatrooms = chatroomRepository.findAll();
        return new ArrayList<>(chatrooms);
    }
    
    /**
     * 根據 senderId 查詢對應的 senderName 並獲取聊天室 ID
     * 如果不存在並 createNewRoomIfNotExists 為 true，則創建新聊天室
     */
    public Optional<String> getChatroomId(
            Integer senderId, // 傳入 senderId
            boolean createNewRoomIfNotExists
    ) {
        // 根據 senderId 獲取 MemberVO 的 name
        String senderName = memberService.getMemberNameById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        return chatroomRepository.findBySenderNameAndRecipientId(senderName, "Adm")
                .map(Chatroom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                    	System.out.println("Creating new chatroom...");
                        var chatId = createChatId(senderName);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
        
    }

    /**
     * 創建新的聊天室 ID
     * @param senderName 發送者名稱
     * @return 聊天室 ID
     */
    private String createChatId(String senderName) {
        String chatId = String.format("%s_%s", senderName, "Adm");

        // 創建正確的 MemberVO 並設置名稱
        MemberVO sender = new MemberVO();
        sender.setName(senderName);

        // 創建 Chatroom 對象並設置 sender 和 chatId
        Chatroom chatroom = Chatroom.builder()
                .chatId(chatId)
                .sender(sender)
                .build();

        System.out.println("Creating new Chatroom with sender name: " + senderName);
        chatroomRepository.save(chatroom); // 儲存到 Redis

        return chatId;
    }

    /**
     * 根據聊天室ID獲取聊天室
     * @param chatId 聊天室ID
     * @return 聊天室資訊
     */
    public Optional<Chatroom> getChatroomByChatId(String chatId) {
        return chatroomRepository.findByChatId(chatId);
    }
    
}
