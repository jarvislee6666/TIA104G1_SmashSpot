package com.smashspot.websocketchat.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.smashspot.admin.model.AdmService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	
	
	private final UserService service;
	

	// @SendTo當標註的方法處理完消息後，返回的結果會被自動發送到 /user/public，所有訂閱者都能收到這條消息
	// @Payload將前端返回的Json數據轉換為Java Object(User)，自動處理序列化/反序列化
	
	// 處理用戶上線，管理員登入系統調用
	// 對應到html的 connect() 函數中通過 stompClient.send("/app/user.addUser"...) 觸發
	@MessageMapping("/user.addUser")
		@SendTo("/user/public") 
		public User addUser(@Payload User user) {
			service.saveUser(user);
			return user;
		}

	
	// 處理用戶離線，點擊登出按鈕時觸發
	// 對應到hmtl的 handleLogout() 調用
		@MessageMapping("/user.disconnectUser")
		@SendTo("/user/public")
		public User disconnect(@Payload User user) {
			service.disconnect(user);
			return user;
		}
		
	// 獲取在線用戶列表，管理員成功連接後調用
	// chatroomAdm.html通過 fetchAndDisplayUserList() 函數獲取用戶聊天列表
		@GetMapping("/users")
		public ResponseEntity<List<String>> findConnectdUsers(){
			return ResponseEntity.ok(service.findConnectUsers());
		}

}
