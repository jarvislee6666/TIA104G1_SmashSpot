package com.smashspot.websocketchat.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.gson.Gson;
import com.smashspot.admin.model.AdmVO
;
import com.smashspot.websocketchat.chat.ChatMessage;
import com.smashspot.websocketchat.chat.ChatMessageService;
import com.smashspot.websocketchat.chat.ChatNotification;
import com.smashspot.websocketchat.chat.HttpSessionConfigurator;
import com.smashspot.websocketchat.chatroom.Chatroom;
import com.smashspot.websocketchat.chatroom.ChatroomService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
//@ServerEndpoint(value = "adm/websocket/chat/{username}", configurator = HttpSessionConfigurator.class)
public class ChatController {
   
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatroomService chatroomService;
    
    
    /**
     * 發送聊天訊息，管理員傳送訊息觸發
     */
    @MessageMapping("/adm/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        // 保存訊息
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // 發送通知
        messagingTemplate.convertAndSendToUser(
                "Adm", // 固定發送給管理員
                "/queue/messages",
                ChatNotification.builder()
                        .id(savedMessage.getId())
                        .senderName(savedMessage.getSenderName())
                        .recipientId(savedMessage.getRecipientId())
                        .content(savedMessage.getContent())
                        .build()
        );
    }

    /**
     * 獲取歷史聊天紀錄，打開特定用戶的聊天視窗時觸發
     */
    @GetMapping("/adm/messages/{senderId}/Adm")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable Integer senderId,
            @PathVariable String recipientId) {

        // 確保 recipientId 為 "Adm"
        if (!"Adm".equals(recipientId)) {
            return ResponseEntity.badRequest().build();
        }

        // 查詢訊息
        List<ChatMessage> messages = chatMessageService.findChatMessages(senderId, recipientId);
        
        // 更新訊息已讀狀態
        messages.stream()
            .filter(msg -> !msg.isRead() && !msg.getSenderName().equals("Adm"))
            .forEach(msg -> chatMessageService.markAsRead(msg.getId()));
        return ResponseEntity.ok(messages);
    }
    

    
    /**
     * 聊天室列表頁面
     */
    @GetMapping("/adm/chat/rooms")
    public String getChatRooms(Model model, HttpSession session) {
        // 驗證管理員登入
        AdmVO admin = (AdmVO) session.getAttribute("loginAdm");
        if (admin == null) {
            return "redirect:/adm/login";
        }

        // 獲取所有聊天室
        List<Chatroom> chatrooms = chatroomService.getAllChatrooms();

        // 合併查詢所有聊天室的訊息
        Map<String, List<ChatMessage>> messagesMap = chatrooms.stream()
                .filter(chatroom -> chatroom.getSender() != null)
                .collect(Collectors.toMap(
                        Chatroom::getChatId,
                        chatroom -> chatMessageService.findChatMessages(chatroom.getSender().getMemid(), "Adm")
                ));

        // 設置聊天室資料
        List<Chatroom> chatroomDTOs = chatrooms.stream()
                .filter(chatroom -> chatroom.getSender() != null)
                .map(chatroom -> {
                    List<ChatMessage> messages = messagesMap.getOrDefault(chatroom.getChatId(), List.of());
                    String lastMessage = messages.isEmpty() ? "" : messages.get(messages.size() - 1).getContent();
                    long unreadCount = messages.stream()
                            .filter(msg -> !msg.isRead() && !msg.getSenderName().equals("Adm"))
                            .count();

                    return Chatroom.builder()
                            .chatId(chatroom.getChatId())
                            .sender(chatroom.getSender())
                            .lastMessage(lastMessage)
                            .unreadCount((int) unreadCount)
                            .build();
                })
                .collect(Collectors.toList());

        model.addAttribute("chatrooms", chatroomDTOs);
        return "adm/chat/rooms";
    }
}
