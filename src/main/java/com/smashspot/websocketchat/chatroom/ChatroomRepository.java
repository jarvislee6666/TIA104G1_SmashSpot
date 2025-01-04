package com.smashspot.websocketchat.chatroom;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.smashspot.websocketchat.user.User;

public interface ChatroomRepository extends CrudRepository<Chatroom, String>{

	Optional<Chatroom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
