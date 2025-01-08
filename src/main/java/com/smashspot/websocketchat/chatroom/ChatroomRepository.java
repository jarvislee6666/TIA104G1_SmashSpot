//package com.smashspot.websocketchat.chatroom;
//
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//import java.util.Optional;
//import javax.annotation.PostConstruct;
//
//@Repository
//public class ChatroomRepository {
//
//    private static final String HASH_KEY = "Chatrooms";
//
//    //在RedisConfig中實作，用來支援序列化/反序列化
//    private final RedisTemplate<String, Chatroom> redisTemplate;
//    //HashOperations<HASH_KEY, key, value>
//    private HashOperations<String, String, Chatroom> hashOperations;
//
//    public ChatroomRepository(RedisTemplate<String, Chatroom> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @PostConstruct
//    private void init() {
//        this.hashOperations = redisTemplate.opsForHash();
//    }
//
//    // 如果數據存在，返回一個 Optional 包裝的 Chatroom；如果不存在，返回 Optional.empty()
//    public Optional<Chatroom> findBySenderIdAndRecipientId(String senderId, String recipientId) {
//        String key = generateKey(senderId, recipientId);
//        return Optional.ofNullable(hashOperations.get(HASH_KEY, key));
//    }
//
//    public void save(Chatroom chatroom) {
//        String key = generateKey(chatroom.getSenderId(), chatroom.getRecipientId());
//        hashOperations.put(HASH_KEY, key, chatroom);
//    }
//
//    private String generateKey(String senderId, String recipientId) {
//        return String.format("%s_%s", senderId, recipientId);
//    }
//}