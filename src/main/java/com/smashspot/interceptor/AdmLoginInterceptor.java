package com.smashspot.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdmLoginInterceptor implements HandlerInterceptor {
	 @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	        HttpSession session = request.getSession();
	        Object loginAdm = session.getAttribute("loginAdm");
	        
	        if (loginAdm == null) {
	        	session.invalidate();
	            response.sendRedirect("/adm/login");
	            return false;
	        }
	        
	        long now = System.currentTimeMillis();
	        long lastAccessTime = session.getLastAccessedTime();
	        if ((now - lastAccessTime) > 600000) {
	            session.invalidate();
	            response.sendRedirect("/adm/login");
	            return false;
	        }
	        
	        return true;
	    }

}
