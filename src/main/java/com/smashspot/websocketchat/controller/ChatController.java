package com.smashspot.websocketchat.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smashspot.member.model.MemberVO;
import com.smashspot.websocketchat.chat.ChatMessage;
import com.smashspot.websocketchat.chat.ChatMessageService;
import com.smashspot.websocketchat.chatroom.Chatroom;
import com.smashspot.websocketchat.chatroom.ChatroomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
   
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatroomService chatroomService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用於序列化 JSON

//    /**
//     * 動態獲取聊天歷史記錄
//     */
//    @MessageMapping("/chat.getHistory")
//    public void getChatHistory(@Payload Map<String, String> request) {
//        try {
//            // 從請求中獲取發送者和接收者ID
//            String senderId = request.get("senderId");
//            String recipientId = request.get("recipientId");
//            
//            if (senderId == null || recipientId == null) {
//                System.err.println("Missing senderId or recipientId");
//                return;
//            }
//
//            List<ChatMessage> chatHistory;
//            
//            // 判斷請求來源
//            if ("Adm".equals(senderId)) {
//                // 管理員查看特定會員的聊天記錄
//                chatHistory = chatMessageService.findChatMessages(
//                    Integer.valueOf(recipientId),  // 會員ID
//                    "Adm"                         // 固定接收者
//                );
//            } else {
//                // 會員查看與管理員的聊天記錄
//                chatHistory = chatMessageService.findChatMessages(
//                    Integer.valueOf(senderId),     // 會員ID
//                    "Adm"                         // 固定接收者
//                );
//            }
//
//            // 更新已讀狀態 (只更新非管理員發送的未讀訊息)
//            chatHistory.stream()
//                .filter(msg -> !msg.isRead() && 
//                        msg.getSenderName() != null && 
//                        !msg.getSenderName().equals("Adm"))
//                .forEach(msg -> chatMessageService.markAsRead(msg.getId()));
//
//            // 依據請求來源決定發送目標
//            String targetUser = "Adm".equals(senderId) ? senderId : String.valueOf(senderId);
//            
//            // 發送歷史訊息
//            messagingTemplate.convertAndSendToUser(
//                targetUser,           // 目標用戶ID
//                "/queue/history",     // 訂閱路徑
//                chatHistory          // 消息內容
//            );
//            
//            // 日誌記錄
//            System.out.println("Chat history sent to user: " + targetUser);
//            System.out.println("Number of messages: " + chatHistory.size());
//            
//        } catch (NumberFormatException e) {
//            System.err.println("Invalid senderId format: " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("Error processing chat history: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    
    
    /** 
     * 發送聊天訊息，會員傳送訊息觸發
     * @param chatMessage 聊天訊息物件
     */
    @MessageMapping("/chat")
    public void processMemberMessage(@Payload ChatMessage chatMessage) {
        // 保存訊息
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        try {
            // 將訊息序列化為 JSON
            String messageJson = objectMapper.writeValueAsString(savedMessage);

            // 發送訊息給管理員
            messagingTemplate.convertAndSendToUser(
                "Adm", // 管理員 ID
                "/queue/messages", // 訂閱路徑
                messageJson // 發送 JSON 格式的訊息
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * 發送聊天訊息，管理員傳送訊息觸發
     * @param chatMessage 聊天訊息物件
     */
    @MessageMapping("/adm/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getSender() == null) {
            // 如果 sender 為空，則建立管理員 VO
            MemberVO adminSender = new MemberVO();
            adminSender.setMemid(0);
            adminSender.setName("Adm");
            chatMessage.setSender(adminSender);
        }

        // 保存訊息
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        try {
            // 將訊息序列化為 JSON
            String messageJson = objectMapper.writeValueAsString(savedMessage);

            // 發送訊息給會員
            messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), // 接收者 ID
                "/queue/messages", // 訂閱路徑
                messageJson // 發送 JSON 格式的訊息
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /** 
     * 會員聊天室頁面
     * @param senderId 發送者 ID
     * @param model 模型對象，用於傳遞數據到前端
     * @param session 當前用戶會話
     * @return 聊天室頁面名稱
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

    	// 獲取聊天訊息
    	List<ChatMessage> messages;
        messages = chatMessageService.findChatMessages(senderId, recipientId);

        model.addAttribute("chatMessages", messages);
        model.addAttribute("senderId", senderId);

        
        return "back-end/chatroom/chatroomMem";
        
    }

    
    /** 
     * 管理員聊天室頁面
     * @param senderId 發送者 ID
     * @param model 模型對象，用於傳遞數據到前端
     * @return 聊天室頁面名稱
     */
    @GetMapping("/adm/chat/{senderId}/Adm")
    public String findChatMessages(@PathVariable Integer senderId, Model model) {
        String recipientId = "Adm";

        if (senderId == null) {
            // 處理空值情況，可以重定向到列表頁面
            return "redirect:/adm/listAllChat";
        }
        
        // 獲取聊天訊息
        List<ChatMessage> messages;
        messages = chatMessageService.findChatMessages(senderId, recipientId);

        model.addAttribute("chatMessages", messages);
        model.addAttribute("senderId", senderId);

        return "back-end/adm/chatroomAdm";
        
    }
//    /**
//     * 會員聊天室頁面
//     */
//    @GetMapping("/chat/Adm/{senderId}")
//    public String findAdmMessages(@PathVariable Integer senderId, Model model, HttpSession session) {
//        MemberVO loginMember = (MemberVO) session.getAttribute("login");
//        
//        if (loginMember == null || !loginMember.getMemid().equals(senderId)) {
//            return "redirect:/member/login";
//        }
//        
//        model.addAttribute("senderId", senderId);
//        return "back-end/chatroom/chatroomMem";
//    }
//
//    /**
//     * 管理員聊天室頁面
//     */
//    @GetMapping("/adm/chat/{senderId}/Adm")
//    public String findChatMessages(@PathVariable Integer senderId, Model model) {
//        if (senderId == null) {
//            return "redirect:/adm/listAllChat";
//        }
//        
//        model.addAttribute("senderId", senderId);
//        return "back-end/adm/chatroomAdm";
//    }
    
    
    /** 
     * 管理員聊天室列表
     * @param model 模型對象，用於傳遞數據到前端
     * @param session 當前用戶會話
     * @return 聊天室列表頁面名稱
     */
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
                    String lastMessage = messages.isEmpty() ? "" : messages.get(messages.size() - 1).getContent();
                    
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
    
    
    /** 
     * 標記管理員的所有消息為已讀
     * @param senderId 發送者 ID
     */
    @MessageMapping("/adm/chat.read")
    public void markMessagesAsRead(@Payload String senderId) {
        try {
            log.info("Processing read status for sender: {}", senderId);
            
            // 獲取聊天室 ID
            String chatId = chatroomService.getChatroomId(
                Integer.valueOf(senderId), 
                false, 
                false
            ).orElse(null);
            
            if (chatId == null) {
                log.warn("No chatroom found for sender: {}", senderId);
                return;
            }
            
            // 標記消息為已讀
            chatMessageService.markAsRead(chatId);
            
            // 發送更新通知
            messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/read.status",
                "Messages marked as read"
            );
            
        } catch (Exception e) {
            log.error("Error marking messages as read for sender: " + senderId, e);
        }
    }
}