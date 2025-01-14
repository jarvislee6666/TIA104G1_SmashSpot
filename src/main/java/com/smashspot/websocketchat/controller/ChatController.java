package com.smashspot.websocketchat.controller;

import java.util.List;
import java.util.Map;
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
import com.smashspot.websocketchat.chat.HttpSessionConfigurator;
import com.smashspot.websocketchat.chatroom.Chatroom;
import com.smashspot.websocketchat.chatroom.ChatroomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
//@ServerEndpoint(value = "adm/websocket/chat/{admname}", configurator = HttpSessionConfigurator.class)
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
    
    @GetMapping("/chat/Adm/{senderId}")
    public String findAdmMessages(@PathVariable Integer senderId, Model model) {
        String recipientId = "Adm";

        // 測試階段：啟用假資料
        boolean useMockData = true;

        List<ChatMessage> messages;
        if (useMockData) {
            // 根據 senderId 返回假資料
            messages = mockChatMessages("chat" + senderId);
        } else {
            // 實際查詢
            messages = chatMessageService.findChatMessages(senderId, recipientId);
        }

        // 更新訊息已讀狀態
        messages.stream()
            .filter(msg -> !msg.isRead() && !msg.getSenderName().equals("Adm"))
            .forEach(msg -> chatMessageService.markAsRead(msg.getId()));

        model.addAttribute("chatMessages", messages);
        model.addAttribute("senderId", senderId);

        // 返回 Thymeleaf 模板名稱（例如 chatView.html）
        return "back-end/chatroom/chatroomMem";
        
    }


//    /**
//     * 獲取歷史聊天紀錄，打開特定用戶的聊天視窗時觸發
//     */
//    @GetMapping("/adm/chat/{senderId}/Adm")
//    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable Integer senderId) {
//        String recipientId = "Adm";
//
//        // 查詢訊息
//        List<ChatMessage> messages = chatMessageService.findChatMessages(senderId, recipientId);
//        
//        // 更新訊息已讀狀態
//        messages.stream()
//            .filter(msg -> !msg.isRead() && !msg.getSenderName().equals("Adm"))
//            .forEach(msg -> chatMessageService.markAsRead(msg.getId()));
//        return ResponseEntity.ok(messages);
//    }
    @GetMapping("/adm/chat/{senderId}/Adm")
    public String findChatMessages(@PathVariable Integer senderId, Model model) {
        String recipientId = "Adm";

        // 測試階段：啟用假資料
        boolean useMockData = true;

        List<ChatMessage> messages;
        if (useMockData) {
            // 根據 senderId 返回假資料
            messages = mockChatMessages("chat" + senderId);
        } else {
            // 實際查詢
            messages = chatMessageService.findChatMessages(senderId, recipientId);
        }

        // 更新訊息已讀狀態
        messages.stream()
            .filter(msg -> !msg.isRead() && !msg.getSenderName().equals("Adm"))
            .forEach(msg -> chatMessageService.markAsRead(msg.getId()));

        model.addAttribute("chatMessages", messages);
        model.addAttribute("senderId", senderId);

        // 返回 Thymeleaf 模板名稱（例如 chatView.html）
        return "back-end/adm/chatroomAdm";
        
    }

    
//    /**
//     * 聊天室列表頁面
//     */
//    @GetMapping("/adm/listAllChat")
//    public String getChatRooms(Model model, HttpSession session) {
////        // 驗證管理員登入
////        AdmVO admin = (AdmVO) session.getAttribute("loginAdm");
////        if (admin == null) {
////            return "redirect:/adm/login";
////        }
//
//        // 獲取聊天室列表
//        List<Chatroom> chatrooms = chatroomService.getAllChatrooms();
//
//        // 查詢所有聊天室的訊息
//        Map<String, List<ChatMessage>> messagesMap = chatrooms.stream()
//                .filter(chatroom -> chatroom.getSender() != null)
//                .collect(Collectors.toMap(
//                        Chatroom::getChatId,
//                        chatroom -> chatMessageService.findChatMessages(chatroom.getSender().getMemid(), "Adm")
//                ));
//
//        // 設置聊天室資料
//        List<Chatroom> chatroomDTOs = chatrooms.stream()
//                .filter(chatroom -> chatroom.getSender() != null)
//                .map(chatroom -> {
//                    List<ChatMessage> messages = messagesMap.getOrDefault(chatroom.getChatId(), List.of());
//                    String lastMessage = messages.isEmpty() ? "" : messages.get(messages.size() - 1).getContent();
//                    long unreadCount = messages.stream()
//                            .filter(msg -> !msg.isRead() && !msg.getSenderName().equals("Adm"))
//                            .count();
//
//                    return Chatroom.builder()
//                            .chatId(chatroom.getChatId())
//                            .sender(chatroom.getSender())
//                            .lastMessage(lastMessage)
//                            .unreadCount((int) unreadCount)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        model.addAttribute("chatrooms", chatroomDTOs);
//        return "/adm/listAllChat";
//    }
    @GetMapping("/adm/listAllChat")
    public String getChatRooms(Model model, HttpSession session) {
        // 測試階段: 假資料 (可以通過檢查環境變數或配置來控制是否啟用假資料)
        boolean useMockData = true; // 設為 false 時禁用假資料

        List<Chatroom> chatrooms;
        if (useMockData) {
            // 添加假資料
            chatrooms = List.of(
            		  Chatroom.builder()
                      .chatId("chat1")
                      .sender(mockMemberVO(1, "John Doe", "johndoe@example.com"))
                      .lastMessage("Hello, how are you?")
                      .unreadCount(3)
                      .build(),
              Chatroom.builder()
                      .chatId("chat2")
                      .sender(mockMemberVO(2, "Jane Smith", "janesmith@example.com"))
                      .lastMessage("See you tomorrow.")
                      .unreadCount(0)
                      .build(),
              Chatroom.builder()
                      .chatId("chat3")
                      .sender(mockMemberVO(3, "Alice Brown", "alicebrown@example.com"))
                      .lastMessage("Meeting at 3 PM.")
                      .unreadCount(1)
                      .build()
            );
        } else {
            // 獲取聊天室列表 (正式資料)
            chatrooms = chatroomService.getAllChatrooms();
        }


        // 查詢所有聊天室的訊息
        Map<String, List<ChatMessage>> messagesMap = chatrooms.stream()
                .filter(chatroom -> chatroom.getSender() != null)
                .collect(Collectors.toMap(
                        Chatroom::getChatId,
                        chatroom -> useMockData
                                ? mockChatMessages(chatroom.getChatId()) // 如果是測試模式，返回假訊息
                                : chatMessageService.findChatMessages(chatroom.getSender().getMemid(), "Adm")
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
        return "back-end/adm/listAllChat";
    }

    // 假資料生成方法
    private List<ChatMessage> mockChatMessages(String chatId) {
        return switch (chatId) {
            case "chat1" -> List.of(
                ChatMessage.builder().content("Hello, how are you?").read(false)
                		.sender(mockMemberVO(1, "John Doe", "johndoe@example.com"))
                		.senderName("John Doe").build(),
                ChatMessage.builder().content("I'm good, thanks!").read(true).senderName("Adm").build()
            );
            case "chat2" -> List.of(
                ChatMessage.builder().content("See you tomorrow.").read(true)
                			.sender(mockMemberVO(2, "Jane Smith", "janesmith@example.com"))
                			.senderName("Jane Smith").build()
            );
            case "chat3" -> List.of(
                ChatMessage.builder().content("Meeting at 3 PM.").read(false)
                			.sender(mockMemberVO(3, "Alice Brown", "alicebrown@example.com"))
                			.senderName("Alice Brown").build(),
                ChatMessage.builder().content("Got it, see you then.").read(true).senderName("Adm").build()
            );
            default -> List.of();
        };
    }
    
 // 假資料生成: 只抓取 MemberVO 的指定欄位
    private MemberVO mockMemberVO(int memid, String name, String email) {
        MemberVO member = new MemberVO();
        member.setMemid(memid);
        member.setName(name);
        member.setEmail(email);
        return member;
    }

}
