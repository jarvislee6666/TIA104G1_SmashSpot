package com.smashspot.websocketchat.chat;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.smashspot.websocketchat.chatroom.Chatroom;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String>{

	List<ChatMessage> findByChatId(String s);
}
