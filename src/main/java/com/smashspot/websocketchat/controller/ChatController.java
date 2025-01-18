package com.smashspot.websocketchat.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.websocket.server.ServerEndpoint;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.smashspot.member.model.MemberVO;
import com.smashspot.websocketchat.chat.ChatMessage;
import com.smashspot.websocketchat.chat.ChatMessageService;
import com.smashspot.websocketchat.chat.ChatNotification;
import com.smashspot.websocketchat.chatroom.Chatroom;
import com.smashspot.websocketchat.chatroom.ChatroomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
   
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatroomService chatroomService;
    
    
    /**
     * 發送聊天訊息，會員傳送訊息觸發
     */
    @MessageMapping("/chat")
    public void processMemberMessage(@Payload ChatMessage chatMessage) {
        // 保存訊息
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // 發送訊息給管理員
        messagingTemplate.convertAndSendToUser(
        	"Adm", // 使用管理員 ID
            "/queue/messages",
            ChatNotification.builder()
            .id(savedMessage.getId())
            .content(savedMessage.getContent())
            .senderName(savedMessage.getSenderName())
//            .sender(savedMessage.getSender())
//            .recipientId(savedMessage.getRecipientId())
            .timestamp(savedMessage.getTimestamp())
            .build() 
        );
    }
    
    
    /**
     * 發送聊天訊息，管理員傳送訊息觸發
     */
    @MessageMapping("/adm/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {

        // 如果sender為空值，則手動建立一個管理員的VO
    	if (chatMessage.getSender() == null) {
            MemberVO adminSender = new MemberVO();
            adminSender.setMemid(0);  
            adminSender.setName("Adm");
            chatMessage.setSender(adminSender);
        }
    	
    	// 保存訊息
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // 發送給指定會員
        messagingTemplate.convertAndSendToUser(
        		 chatMessage.getRecipientId(),   // 使用接收者ID
            "/queue/messages",
            ChatNotification.builder()
                .id(savedMessage.getId())
                .content(savedMessage.getContent())
                .senderName(savedMessage.getSenderName())
//                .sender(savedMessage.getSender())
//                .recipientId(savedMessage.getRecipientId())
                .timestamp(savedMessage.getTimestamp())
                .build()
        );
    }
    
    /**
     * 會員聊天室
     */
    @GetMapping("/chat/Adm/{senderId}")
    public String findAdmMessages(@PathVariable Integer senderId, Model model, HttpSession session) {
    	// 取得當前登入的會員資訊
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        
        // 驗證是否已登入且訪問的是自己的聊天室
        if (loginMember == null || !loginMember.getMemid().equals(senderId)) {
            // 如果未登入或嘗試訪問其他會員的聊天室，重定向到登入頁面或首頁
            return "redirect:/member/login";
        }
    	
    	String recipientId = "Adm";

        List<ChatMessage> messages;
        messages = chatMessageService.findChatMessages(senderId, recipientId);

        // 更新訊息已讀狀態
        messages.stream()
            .filter(msg -> !msg.isRead() && !"Adm".equals(msg.getSenderName()))
            .forEach(msg -> chatMessageService.markAsRead(msg.getId()));

        model.addAttribute("chatMessages", messages);
        model.addAttribute("senderId", senderId);

        // 返回 Thymeleaf 模板名稱（例如 chatView.html）
        return "back-end/chatroom/chatroomMem";
        
    }

    /**
     * 管理員聊天室
     */
    @GetMapping("/adm/chat/{senderId}/Adm")
    public String findChatMessages(@PathVariable Integer senderId, Model model) {
        String recipientId = "Adm";

        if (senderId == null) {
            // 處理空值情況，可以重定向到列表頁面
            return "redirect:/adm/listAllChat";
        }
        
        List<ChatMessage> messages;
        messages = chatMessageService.findChatMessages(senderId, recipientId);

        // 更新訊息已讀狀態
        messages.stream()
				.filter(msg -> !msg.isRead() && msg.getSenderName() != null && !"Adm".equals(msg.getSenderName()))
				.forEach(msg -> chatMessageService.markAsRead(msg.getId()));

        model.addAttribute("chatMessages", messages);
        model.addAttribute("senderId", senderId);

        // 返回 Thymeleaf 模板名稱（例如 chatView.html）
        return "back-end/adm/chatroomAdm";
        
    }
    
    @GetMapping("/adm/listAllChat")
    public String getChatRooms(Model model, HttpSession session) {
        // 獲取聊天室列表
        List<Chatroom> chatrooms = chatroomService.getAllChatrooms();

        // 查詢所有聊天室的訊息
        List<Chatroom> chatroomDTOs = chatrooms.stream()
                .filter(chatroom -> chatroom.getSender() != null)
                .map(chatroom -> {
                    // 獲取該聊天室的所有訊息
                    List<ChatMessage> messages = chatMessageService.findChatMessages(
                        chatroom.getSender().getMemid(), 
                        "Adm"
                    );
                    
                    // 獲取最後一條訊息內容
                    String lastMessage = messages.isEmpty() ? "" : messages.get(0).getContent();
                    
                    // 計算未讀訊息數
                    long unreadCount = messages.stream()
                            .filter(msg -> !msg.isRead() && 
                                         msg.getSenderName() != null && 
                                         !msg.getSenderName().equals("Adm"))
                            .count();

                    // 建立新的聊天室物件
                    return Chatroom.builder()
                    		.id(UUID.randomUUID().toString())
                            .chatId(chatroom.getChatId())
                            .sender(chatroom.getSender())
                            .senderName(chatroom.getSenderName())
                            .recipientId(chatroom.getRecipientId())
                            .lastMessage(lastMessage)
                            .unreadCount((int) unreadCount)
                            .build();
                })
                .collect(Collectors.toList());

        model.addAttribute("chatrooms", chatroomDTOs);
        return "back-end/adm/listAllChat";
    }
}