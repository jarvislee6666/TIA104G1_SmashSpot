package com.smashspot.websocketchat.chatroom;

import java.util.Optional;

public class MockChatroomService extends ChatroomService {

    public MockChatroomService() {
        super(null, null);
    }

    @Override
    public Optional<String> getChatroomId(Integer senderId, boolean createNewRoomIfNotExists) {
        if (senderId == 1) {
            return Optional.of("JohnDoe_Adm");
        }
        return createNewRoomIfNotExists ? Optional.of("NewRoom_Adm") : Optional.empty();
    }
}
