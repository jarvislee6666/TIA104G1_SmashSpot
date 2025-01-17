package com.smashspot.websocketchat.controller;

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

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ServerEndpoint(value = "/mem/websocket/chat/{memid}", configurator = HttpSessionConfigurator.class)
public class MemChatController {

	private final ChatMessageService chatMessageService;
	private static final Map<String, Session> sessionsMap = new ConcurrentHashMap<>();
	private final Gson gson = new Gson();

	/**
	 * WebSocket 連接建立時的回調
	 */
	@OnOpen
    public void onOpen(@PathParam("memid") String memid, Session session, EndpointConfig config) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSessionConfigurator.class.getName());
        System.out.println("Received memid: " + memid);
        // Verify member session and extract member details
        if (httpSession == null || httpSession.getAttribute("login") == null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized member"));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        Integer memId = (Integer) httpSession.getAttribute("memId");
        String memName = (String) httpSession.getAttribute("memName");
        
        if (memId == null || !memid.equals(memId.toString())) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid member ID"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        sessionsMap.put(memid, session);
        
        System.out.println("Member " + memName + " (ID: " + memid + ") connected");
    }
	/**
	 * WebSocket 接收到訊息時的回調
	 */
	  @OnMessage
	    public void onMessage(String message, Session session, @PathParam("memid") String memberId) {
		  try {
		        ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
		        chatMessageService.save(chatMessage);
		        
		        // 修改：發送給管理員
		        Session adminSession = sessionsMap.get("Adm"); // 使用固定的管理員 ID
		        if (adminSession != null && adminSession.isOpen()) {
		            adminSession.getBasicRemote().sendText(message);
		        }
		    } catch (Exception e) {
		        System.err.println("Error processing message from member: " + memberId);
		        e.printStackTrace();
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
