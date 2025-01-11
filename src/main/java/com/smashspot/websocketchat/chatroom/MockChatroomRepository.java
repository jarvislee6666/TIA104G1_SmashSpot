package com.smashspot.websocketchat.chatroom;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MockChatroomRepository extends ChatroomRepository {

    private final Map<String, Chatroom> storage = new HashMap<>();

    public MockChatroomRepository() {
        super(null); // 因為是模擬類，不需要 RedisTemplate
    }

    @Override
    public Optional<Chatroom> findBySenderNameAndRecipientId(String senderName, String recipientId) {
        String key = generateKey(senderName, recipientId);
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public void save(Chatroom chatroom) {
        String key = generateKey(chatroom.getSenderName(), chatroom.getRecipientId());
        System.out.println("Saving Chatroom with key: " + key);
        System.out.println("Chatroom details: " + chatroom);
        storage.put(key, chatroom);
    }

    private String generateKey(String senderName, String recipientId) {
    	String key = String.format("%s_%s", senderName, recipientId);
        System.out.println("Generated key: " + key);
        return key;
    }
}
