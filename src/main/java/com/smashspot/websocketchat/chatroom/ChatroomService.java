package com.smashspot.websocketchat.chatroom;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatroomService {
	
	private final ChatroomRepository chatroomRepository;
	
	public Optional<String> getChatroomId(
			String senderId,
			String recipientId,
			boolean createNewRoomIfNotExists
	){
		return chatroomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
				.map(Chatroom::getChatId)
				.or(() -> {
					if (createNewRoomIfNotExists) {
						var chatId = createChatId(senderId, recipientId);
						return Optional.of(chatId);
					}
					return Optional.empty();
				});
	}

	private String createChatId(String senderId, String recipientId) {
		var chatId = String.format("%s_%s", senderId, recipientId); //ex: bella_amy
		
		Chatroom senderRecipient = Chatroom.builder()
				.chatId(chatId)
				.senderId(senderId)
				.recipientId(recipientId)
				.build();
		
		Chatroom recipientSender = Chatroom.builder()
				.chatId(chatId)
				.senderId(recipientId)
				.recipientId(senderId)
				.build();
		
		chatroomRepository.save(senderRecipient);
		chatroomRepository.save(recipientSender);
		
		return chatId;
	}

}
