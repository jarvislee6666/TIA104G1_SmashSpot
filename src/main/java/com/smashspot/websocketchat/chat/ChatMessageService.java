package com.smashspot.websocketchat.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
	 */

	public ChatMessage save(ChatMessage chatMessage) {
		  String key = "chat:" + chatMessage.getChatId();
		   System.out.println("Storing message in key: " + key);
		try {
			log.info("Saving chat message: {}", chatMessage);

			// 檢查是否為管理員消息
			boolean isAdmin = "Adm".equals(chatMessage.getSenderName());
			log.info("Message is from admin: {}", isAdmin);

			String senderId;
			String senderName;
			if (isAdmin) {
				// 管理員發送的消息，使用接收者ID
				senderId = chatMessage.getRecipientId();
				senderName = "Adm";
				log.info("Admin sending to mem ID: {}", senderId);
				log.info("Admin sending to mem NAME: {}", senderName);
			} else {
				// 會員發送的消息，使用發送者ID
				senderId = String.valueOf(chatMessage.getSender().getMemid());
				senderName = chatMessage.getSender().getName();
				log.info("Member sending message, mem ID: {}", senderId);
				log.info("Member sending message, mem NAME: {}", senderName);
			}

			// 確保 senderId 不為空且為有效數字
			if (senderId == null || senderId.trim().isEmpty()) {
				throw new IllegalArgumentException("mem ID cannot be null or empty");
			}

			try {
				Integer userIdInt = Integer.parseInt(senderId);
				log.info("Parsed mem ID: {}", userIdInt);

				// 獲取或創建聊天室
				String chatId = chatroomService.getChatroomId(userIdInt, isAdmin, true).orElseThrow(() -> {
					log.error("Failed to create or get chatroom for mem ID: {}", userIdInt);
					return new IllegalStateException("Failed to create or retrieve chatroom for mem: " + userIdInt);
				});

				log.info("Retrieved chat ID: {}", chatId);
				
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

				// 保存消息

				repository.save(newMessage);

				return chatMessage;

			} catch (NumberFormatException e) {
				log.error("Invalid mem ID format: {}", senderId, e);
				throw new IllegalArgumentException("Invalid mem ID format: " + senderId);
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
		  System.out.println("Marking messages as read for chatId: " + chatId);
		    List<ChatMessage> messages = repository.findByChatId(chatId);
		    System.out.println("Found messages: " + messages.size());
		    for (ChatMessage message : messages) {
		        System.out.println("Processing message: " + message.getId());
		        if (!message.isRead()) {
		            message.setRead(true);
		            System.out.println("Marking as read: " + message.getId());
		            repository.save(message);
		        }
		    }
		}

}
