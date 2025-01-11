package com.smashspot.websocketchat.chat;

import java.util.ArrayList;
import java.util.List;

public class MockChatMessageRepository extends ChatMessageRepository {

    private final List<ChatMessage> storage = new ArrayList<>();

    public void save(ChatMessage chatMessage) {
        storage.add(chatMessage);
        System.out.println("Mock saved ChatMessage: " + chatMessage);
    }

    public List<ChatMessage> findByChatId(String chatId) {
        System.out.println("Mock finding messages by ChatId: " + chatId);
        List<ChatMessage> result = new ArrayList<>();
        for (ChatMessage message : storage) {
            if (chatId.equals(message.getChatId())) {
                result.add(message);
            }
        }
        return result;
    }
}
