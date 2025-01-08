//package com.smashspot.websocketchat.chat;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.stereotype.Service;
//
//import com.smashspot.websocketchat.chatroom.ChatroomService;
//
//import lombok.RequiredArgsConstructor;
//import redis.clients.jedis.Jedis;
//
//@Service
//@RequiredArgsConstructor
//public class ChatMessageService {
//
//	private final ChatMessageRepository repository;
////	private final AbcRepository repository;
//	private final ChatroomService chatroomService;
//
//	public ChatMessage save(ChatMessage chatMessage) {
//		var chatId = chatroomService.getChatroomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
//				.orElseThrow(); // 自訂例外處理，最後一個參數為creatNewRoomIfNotExist
//		chatMessage.setChatId(chatId);
//		repository.save(chatMessage);
//		return chatMessage;
//	}
//
//	public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
//		var chatId = chatroomService.getChatroomId(senderId, recipientId, false); // 自訂例外處理，最後一個參數為creatNewRoomIfNotExist
//		return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
//	}
//
//
//}
