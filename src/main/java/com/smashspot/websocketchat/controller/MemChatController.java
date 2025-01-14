package com.smashspot.websocketchat.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
//import com.smashspot.admin.model.AdmVO;
import com.smashspot.websocketchat.chat.ChatMessage;
import com.smashspot.websocketchat.chat.ChatMessageService;
import com.smashspot.websocketchat.chat.HttpSessionConfigurator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ServerEndpoint(value = "/mem/websocket/chat/{memname}", configurator = HttpSessionConfigurator.class)
public class MemChatController {

	private final ChatMessageService chatMessageService;
	private static final Map<String, Session> sessionsMap = new ConcurrentHashMap<>();
	private final Gson gson = new Gson();

	/**
	 * WebSocket 連接建立時的回調
	 */
	@OnOpen
	public void onOpen(@PathParam("memname") String memname, Session session, EndpointConfig config) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		 try {
		        // 解碼中文字符
		        String decodedName = URLDecoder.decode(memname, StandardCharsets.UTF_8.name());
		        System.out.println("Decoded member name: " + decodedName);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
//		if (httpSession == null || httpSession.getAttribute("login") == null) {
//			try {
//				session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized member"));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return;
//		}

		// 驗證成功，繼續處理
		System.out.println("HttpSession: " + httpSession);
		sessionsMap.put(memname, session);
		System.out.println(memname + " connected as member.");
	}

	/**
	 * WebSocket 接收到訊息時的回調
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
			chatMessageService.save(chatMessage);

			// 發送給接收者
			String receiver = chatMessage.getRecipientId();
			Session receiverSession = sessionsMap.get(receiver);
			if (receiverSession != null && receiverSession.isOpen()) {
				receiverSession.getBasicRemote().sendText(message);
			} else {
				// 保存為離線訊息
				System.out.println("Receiver " + receiver + " is offline. Storing message.");
				chatMessageService.save(chatMessage);
			}
		} catch (Exception e) {
			System.err.println("Error processing message: " + e.getMessage());
		}
	}

	/**
	 * WebSocket 錯誤處理
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		System.err.println("WebSocket error: " + throwable.getMessage());
	}

	/**
	 * WebSocket 連接關閉時的回調
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		String disconnectedUser = null;
		for (Map.Entry<String, Session> entry : sessionsMap.entrySet()) {
			if (entry.getValue().equals(session)) {
				disconnectedUser = entry.getKey();
				sessionsMap.remove(entry.getKey());
				break;
			}
		}
		if (disconnectedUser != null) {
			System.out.println(disconnectedUser + " disconnected. Reason: " + closeReason.getReasonPhrase());
		}
	}

}
