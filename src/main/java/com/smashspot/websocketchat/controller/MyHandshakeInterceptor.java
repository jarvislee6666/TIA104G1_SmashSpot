package com.smashspot.websocketchat.controller;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class MyHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res, WebSocketHandler handler,
            Map<String, Object> attributes) throws Exception {
        
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) req;
        
        // Check for member session
        if (servletRequest.getServletRequest().getSession().getAttribute("login") != null) {
            Integer memId = (Integer) servletRequest.getServletRequest().getSession().getAttribute("memId");
            String memName = (String) servletRequest.getServletRequest().getSession().getAttribute("memName");
            attributes.put("memId", memId);
            attributes.put("memName", memName);
            System.out.println("Member handshake successful: " + memId);
            return true;
        }
        
        // Check for admin session
        if (servletRequest.getServletRequest().getSession().getAttribute("loginAdm") != null) {
            Integer admId = (Integer) servletRequest.getServletRequest().getSession().getAttribute("admId");
            String admName = (String) servletRequest.getServletRequest().getSession().getAttribute("admName");
            attributes.put("admId", admId);
            attributes.put("admName", admName);
            System.out.println("Admin handshake successful: " + admId);
            return true;
        }
        
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception ex) {
        // Post-handshake logic if needed
    }
}
