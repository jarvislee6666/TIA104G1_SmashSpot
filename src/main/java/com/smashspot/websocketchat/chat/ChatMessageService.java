package com.smashspot.websocketchat.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smashspot.member.model.MemberVO;
import com.smashspot.websocketchat.chatroom.ChatroomRepository;
import com.smashspot.websocketchat.chatroom.ChatroomService;

import lombok.RequiredArgsConstructor;

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
		// 確保 senderId 存在
		Integer senderId = Optional.ofNullable(chatMessage.getSender()).map(MemberVO::getMemid) // 提取 MemberVO 的 memid
				.orElseThrow(() -> new IllegalArgumentException("Sender ID not found"));

		// 獲取或創建聊天室 ID
		var chatId = chatroomService.getChatroomId(senderId, true)
				.orElseThrow(() -> new IllegalStateException("Failed to create or retrieve chatroom"));

		// 設置 ChatMessage 的 ChatId 並保存
		chatMessage.setChatId(chatId);
		repository.save(chatMessage);

		return chatMessage;
	}

	/**
	 * 獲取聊天訊息列表
	 */
	public List<ChatMessage> findChatMessages(Integer senderId, String recipientId) {
		// 查詢聊天室 ID
		var chatId = chatroomService.getChatroomId(senderId, false);

		// 如果聊天室存在，查詢訊息；否則返回空列表
		return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
	}
	
	public void markAsRead(String chatId) {
	    List<ChatMessage> messages = repository.findByChatId(chatId);
	    for (ChatMessage message : messages) {
	        if (!message.isRead()) {
	        	message.setRead(true);
	        	repository.save(message);
	        }
	    }
	}

}
