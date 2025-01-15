package com.smashspot.websocketchat.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smashspot.websocketchat.chatroom.ChatroomRepository;
import com.smashspot.websocketchat.chatroom.ChatroomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

	private final ChatMessageRepository repository;
	private final ChatroomRepository roomRepository;
	private final ChatroomService chatroomService;

	/**
	 * 保存聊天訊息
	 */

	public ChatMessage save(ChatMessage chatMessage) {
		try {
			log.info("Saving chat message: {}", chatMessage);

			// 檢查是否為管理員消息
			boolean isAdmin = "Adm".equals(chatMessage.getSenderName());
			log.info("Message is from admin: {}", isAdmin);

			String userId;
			if (isAdmin) {
				// 管理員發送的消息，使用接收者ID
				userId = chatMessage.getRecipientId();
				log.info("Admin sending to user ID: {}", userId);
			} else {
				// 會員發送的消息，使用發送者ID
				userId = String.valueOf(chatMessage.getSender().getMemid());
				log.info("Member sending message, user ID: {}", userId);
			}

			// 確保 userId 不為空且為有效數字
			if (userId == null || userId.trim().isEmpty()) {
				throw new IllegalArgumentException("User ID cannot be null or empty");
			}

			try {
				Integer userIdInt = Integer.parseInt(userId);
				log.info("Parsed user ID: {}", userIdInt);

				// 獲取或創建聊天室
				String chatId = chatroomService.getChatroomId(userIdInt, isAdmin, true).orElseThrow(() -> {
					log.error("Failed to create or get chatroom for user ID: {}", userIdInt);
					return new IllegalStateException("Failed to create or retrieve chatroom for user: " + userIdInt);
				});

				log.info("Retrieved chat ID: {}", chatId);
				chatMessage.setChatId(chatId);

				// 保存消息

				repository.save(chatMessage);

				return chatMessage;

			} catch (NumberFormatException e) {
				log.error("Invalid user ID format: {}", userId, e);
				throw new IllegalArgumentException("Invalid user ID format: " + userId);
			}

		} catch (Exception e) {
			log.error("Error saving chat message", e);
			throw e;
		}
	}

	/**
	 * 獲取聊天訊息列表
	 */
	 public List<ChatMessage> findChatMessages(Integer senderId, String recipientId) {
	        if (senderId == null) {
	            return new ArrayList<>();
	        }
	        // 取得聊天室ID
	        var chatId = chatroomService.getChatroomId(senderId, false);
	        
	        // 查詢訊息並過濾排序
	        return chatId.map(id -> repository.findByChatId(id))
	                    .orElse(new ArrayList<>());
	    }
	    
	    // 標記訊息已讀
	    public void markAsRead(String chatId) {
	        List<ChatMessage> messages = repository.findByChatId(chatId);
	        messages.stream()
	               .filter(msg -> !msg.isRead())
	               .forEach(msg -> {
	                   msg.setRead(true);
	                   repository.save(msg);
	               });
	    }

}
