package com.smashspot.websocketchat.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.websocketchat.chatroom.ChatroomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

	private final ChatMessageRepository repository;
	private final ChatroomService chatroomService;

	
	/** 
     * 保存聊天訊息
     * @param chatMessage 聊天訊息物件
     * @return 已保存的聊天訊息
     */
	@Transactional
	public ChatMessage save(ChatMessage chatMessage) {
	    try {
	        log.info("Saving chat message: {}", chatMessage);

	        // 檢查是否為管理員消息
	        boolean isAdmin = "Adm".equals(chatMessage.getSenderName());
	        log.info("Message is from admin: {}", isAdmin);

	        // 處理發送者信息
	        String senderId;
	        String senderName;
	        
	        if (isAdmin) {
	        	senderId = chatMessage.getRecipientId();	        	
	            senderName = "Adm";
	        } else {
	            senderId = String.valueOf(chatMessage.getSender().getMemid());
	            senderName = chatMessage.getSender().getName();
	        }

	        // 驗證 senderId
	        if (senderId == null || senderId.trim().isEmpty()) {
	            throw new IllegalArgumentException("SenderId cannot be null or empty");
	        }

	        Integer userIdInt = Integer.parseInt(senderId);
	        log.info("Processing message for user ID: {}", userIdInt);

	        // 獲取或創建聊天室 ID
	        String chatId = chatroomService.getChatroomId(userIdInt, isAdmin, true)
	                .orElseGet(() -> {
	                    // 如果沒有找到，嘗試再次創建
	                    log.info("Retrying to create chatroom for user: {}", userIdInt);
	                    return chatroomService.getChatroomId(userIdInt, isAdmin, true)
	                            .orElseThrow(() -> new IllegalStateException(
	                                    "Failed to create chatroom after retry for user: " + userIdInt));
	                });

	        log.info("Successfully got chatId: {}", chatId);

	        // 構建新消息
	        ChatMessage newMessage = ChatMessage.builder()
	                .id(UUID.randomUUID().toString())
	                .chatId(chatId)
	                .sender(chatMessage.getSender())
	                .senderName(senderName)
	                .recipientId(isAdmin ? senderId : "Adm")
	                .content(chatMessage.getContent())
	                .timestamp(new Date())
	                .read(false)
	                .build();

	        // 保存並返回新消息
	        repository.save(newMessage);
	        return chatMessage;  // 返回新保存的消息而不是原始消息

	    } catch (NumberFormatException e) {
	        log.error("Invalid user ID format", e);
	        throw new IllegalArgumentException("Invalid user ID format: " + e.getMessage());
	    } catch (Exception e) {
	        log.error("Error saving chat message", e);
	        throw new RuntimeException("Failed to save chat message", e);
	    }
	}

	
	/** 
     * 獲取聊天訊息列表
     * @param senderId 發送者 ID
     * @param recipientId 接收者 ID
     * @return 聊天訊息列表
     */
	 public List<ChatMessage> findChatMessages(Integer senderId, String recipientId) {
	        if (senderId == null) {
	            return new ArrayList<>();
	        }
	        // 取得聊天室ID
	        var chatId2 = chatroomService.getChatroomId(senderId, false);
	        
	        // 查詢訊息並過濾排序
	        return chatId2.map(chatId -> repository.findByChatId(chatId))
	                    .orElse(new ArrayList<>());
	    }
	    
	 
	 /** 
	  * 標記訊息為已讀
	  * @param chatId 聊天室 ID
	  */
	 @Transactional
	 public void markAsRead(String chatId) {
		    try {
		        log.info("Marking messages as read for chatId: {}", chatId);
		        List<ChatMessage> messages = repository.findByChatId(chatId);
		        
		        boolean hasUpdates = false;
		        List<ChatMessage> updatedMessages = new ArrayList<>();
		        
		        for (ChatMessage message : messages) {
		            if (!message.isRead()) {
		                message.setRead(true);
		                hasUpdates = true;
		                log.info("Marked message {} as read", message.getChatId());
		            }
		            updatedMessages.add(message);
		        }
		        
		        if (hasUpdates) {
		            repository.updateMessages(chatId, updatedMessages);
		            log.info("Updated read status for chatId: {}", chatId);
		        }
		        
		    } catch (Exception e) {
		        log.error("Error marking messages as read", e);
		        throw new RuntimeException("Failed to mark messages as read", e);
		    }
		}

}
