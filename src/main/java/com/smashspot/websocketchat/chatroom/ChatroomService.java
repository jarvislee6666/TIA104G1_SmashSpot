package com.smashspot.websocketchat.chatroom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatroomService {
    private final ChatroomRepository chatroomRepository;
    private final MemberService memberService;

    /**
     * 獲取所有聊天室
     */
    public List<Chatroom> getAllChatrooms() {
        Collection<Chatroom> chatrooms = chatroomRepository.findAll();
        return new ArrayList<>(chatrooms);
    }
    

    /**
     * 根據用戶ID獲取或創建聊天室
     * @param userId 用戶ID（可能是會員ID或管理員ID）
     * @param isAdmin 是否為管理員發送消息
     * @param createNewRoomIfNotExists 如果不存在是否創建新聊天室
     */
    public Optional<String> getChatroomId(
            Integer userId,
            boolean isAdmin,
            boolean createNewRoomIfNotExists
    ) {
        if (userId == null) {
            return Optional.empty();
        }

            // 獲取會員名稱
            String memberName = memberService.getMemberNameById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            // 查找現有聊天室
            return chatroomRepository.findBySenderNameAndRecipientId(memberName, "Adm")
                    .map(Chatroom::getChatId)
                    .or(() -> {
                        if (createNewRoomIfNotExists) {
                            log.info("Creating new chatroom for {} user: {}", 
                                   isAdmin ? "admin" : "member", memberName);
                            return Optional.of(createChatId(memberName, userId));
                        }
                        return Optional.empty();
                    });
        
    }

    /**
     * 向下相容的方法，供現有代碼調用
     */
    public Optional<String> getChatroomId(Integer senderId, boolean createNewRoomIfNotExists) {
        return getChatroomId(senderId, false, createNewRoomIfNotExists);
    }

    /**
     * 創建新的聊天室
     * @param memberName 會員名稱
     * @param memberId 會員ID
     * @return 聊天室ID
     */
    private String createChatId(String memberName, Integer memberId) {
        String chatId = String.format("%s_%s", memberName, "Adm");
        
        // 創建完整的 MemberVO
        MemberVO member = new MemberVO();
        member.setMemid(memberId);
        member.setName(memberName);

        // 創建聊天室
        Chatroom chatroom = Chatroom.builder()
        		 .id(UUID.randomUUID().toString()) // 添加 id 字段
                 .chatId(chatId)
                 .sender(member)
                 .senderName(memberName)  
                 .recipientId("Adm")
                 .lastMessage("")         // 初始化 lastMessage
                 .unreadCount(0)          // 初始化 unreadCount
                 .build();

        log.info("Saving new chatroom: {}", chatroom);
        chatroomRepository.save(chatroom);
        
        return chatId;
    }

    /**
     * 根據聊天室ID獲取聊天室
     */
    public Optional<Chatroom> getChatroomByChatId(String chatId) {
        return chatroomRepository.findByChatId(chatId);
    }
}