//package com.smashspot.websocketchat.user;
//
//import java.util.List;
//222
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequiredArgsConstructor
//public class UserController {
//	
//	
//	private final UserService service;
//	
//	 @MessageMapping("/user.addUser")
//		@SendTo("/user/public")
//		public User addUser(@Payload User user) {
//			service.saveUser(user);
//			return user;
//		}
//
//		@MessageMapping("/user.disconnectUser")
//		@SendTo("/user/public")
//		public User disconnect(@Payload User user) {
//			service.disconnect(user);
//			return user;
//		}
//		
//		@GetMapping("/users")
//		public ResponseEntity<List<String>> findConnectdUsers(){
//			return ResponseEntity.ok(service.findConnectUsers());
//		}
//
//}
