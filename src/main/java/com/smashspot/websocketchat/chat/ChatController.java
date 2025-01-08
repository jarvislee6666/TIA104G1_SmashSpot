//package com.smashspot.websocketchat.chat;
//
//import java.util.List;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController  // 改用 @RestController 因為要返回 JSON 數據
//@RequiredArgsConstructor
//public class ChatController {
//    
//    private final SimpMessagingTemplate messagingTemplate;
//	private final ChatMessageService chatMessageService;
//
//    @MessageMapping("/chat")
//    public void processMessage(@Payload ChatMessage chatMessage) {
//    	ChatMessage saveMsg = chatMessageService.save(chatMessage);
//    	// bella/queue/messages
//    	messagingTemplate.convertAndSendToUser(
//    			chatMessage.getRecipientId(),
//    			"/queue/messages",
//    			ChatNotification.builder()
//    			.id(saveMsg.getId())
//    			.senderId(saveMsg.getSenderId())
//    			.recipientId(saveMsg.getRecipientId())
//    			.content(saveMsg.getContent())
//    			.build()  			
//    			);
//    }
//    
//    @GetMapping("/messages/{senderId}/{recipientId}")
//    public ResponseEntity<List<ChatMessage>> findChatMessages(
//            @PathVariable String senderId,
//            @PathVariable String recipientId) {
//        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
//    }
//}