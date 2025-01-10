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

        // 檢查 session 中是否有 "login" 屬性
        if (session.getAttribute("login") == null) {
        	// 檢查是否為 AJAX 請求
            String xRequestedWith = request.getHeader("X-Requested-With");
            boolean isAjax = "XMLHttpRequest".equals(xRequestedWith);
            
            // 獲取當前請求的URI
            String requestURI = request.getRequestURI();
            
            // 如果是出價競標的檢查請求
            if (requestURI.equals("/api/bid/check-login")) {
                // 從 Referer 獲取原始商品頁面 URL
                String referer = request.getHeader("Referer");
                if (referer != null) {
                    // 保存原始URL到session
                    session.setAttribute("redirectURL", referer);
                }
            }else {
                // 其他請求的原始URL處理
            	// 未登入 → 記下「原請求路徑」，以便登入後能導回此處
                String originalURL = request.getRequestURI(); // /reservation/week
                String queryString = request.getQueryString();// 可能有 ?stdmId=3&week=1 之類
                if (queryString != null) {
                    originalURL = originalURL + "?" + queryString;
                }
                session.setAttribute("redirectURL", originalURL);
            }
            
            
            if (isAjax) {
            	// 對 AJAX 請求返回 JSON 響應
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"isLoggedIn\": false, \"redirect\": \"/member/login\"}");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            } else {
                // 非 AJAX 請求則重導向到登入頁
                response.sendRedirect("/member/login");
                return false;
            }

        }

        // 已登入 → 直接放行
        return true;
    }

}

