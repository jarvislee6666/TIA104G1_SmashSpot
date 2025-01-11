package com.smashspot.websocketchat.user;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	

	private final RedisTemplate<String, Object> redisTemplate;
	
	@PostConstruct
    public void init() {
        System.out.println("RedisTemplate injected: " + (redisTemplate != null));
    }
	 // redisTemplate.opsForHash().put("userStatus", key, value);
	public void saveUser(User user) {
        redisTemplate.opsForHash().put("userStatus", user.getNickName(), Status.ONLINE.name());
    }

	//第三個參數會返回OFFLINE(使用enum類型內建的name方法)
    public void disconnect(User user) {
        redisTemplate.opsForHash().put("userStatus", user.getNickName(), Status.OFFLINE.name());
    }

    public List<String> findConnectUsers() {
    	//Map<key, value>, key = nickName, value = Status
        Map<Object, Object> allUsers = redisTemplate.opsForHash().entries("userStatus");
        
        //每個entry包含一個鍵值對，將entry轉換
        return allUsers.entrySet().stream()
            .filter(entry -> Status.ONLINE.name().equals(entry.getValue()))
            .map(entry -> entry.getKey().toString())
            .collect(Collectors.toList());
    }
}
