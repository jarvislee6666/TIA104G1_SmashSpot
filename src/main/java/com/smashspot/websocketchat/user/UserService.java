//package com.smashspot.websocketchat.user;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//	
//	private final UserRepository repository;
//	
//
//    public void saveUser(User user) {
//		user.setStatus(Status.ONLINE);
//		repository.save(user);
//		
//	}
//	
//	public void disconnect(User user) {
//		var storedUser = repository.findById(user.getNickName())
//				.orElse(null);
//		if(storedUser != null) {
//			storedUser.setStatus(Status.OFFLINE);
//			repository.save(storedUser);
//		}
//		
//	}
//	
//	public List<User> findConnectUsers(){
//		return repository.findAllByStatus(Status.ONLINE);
//	}
//}
