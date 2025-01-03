package com.smashspot.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    HttpSession session = request.getSession();
	    if (session.getAttribute("login") == null) {
	        String requestUri = request.getRequestURI();
	        String queryString = request.getQueryString();
	        String redirectUrl = queryString != null ? requestUri + "?" + queryString : requestUri;
	        session.setAttribute("redirectUrl", redirectUrl);
	        response.sendRedirect(request.getContextPath() + "/member/login");
	        return false;
	    }
	    return true;
	}
}
