package com.smashspot.websocketchat.chat;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession != null) {
            // 存儲 HttpSession 到 config 中，使用與 Controller 一致的 key
            config.getUserProperties().put(HttpSessionConfigurator.class.getName(), httpSession);
            
            // 檢查管理員登入
            Object loginAdm = httpSession.getAttribute("loginAdm");
            if (loginAdm != null) {
                Integer admId = (Integer) httpSession.getAttribute("admId");
                String admName = (String) httpSession.getAttribute("admName");
                System.out.println("Admin handshake - ID: " + admId + ", Name: " + admName);
            }
            
            // 檢查會員登入
            Object login = httpSession.getAttribute("login");
            if (login != null) {
                Integer memId = (Integer) httpSession.getAttribute("memId");
                String memName = (String) httpSession.getAttribute("memName");
                System.out.println("Member handshake - ID: " + memId + ", Name: " + memName);
            }
        } else {
            System.err.println("No HTTP session found during handshake");
        }
    }
}