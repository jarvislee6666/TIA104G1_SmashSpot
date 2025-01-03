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
            // 未登入 → 記下「原請求路徑」，以便登入後能導回此處
            String originalURL = request.getRequestURI();      // /reservation/week
            String queryString = request.getQueryString();     // 可能有 ?stdmId=3&week=1 之類
            if (queryString != null) {
                originalURL = originalURL + "?" + queryString; 
            }
            session.setAttribute("redirectURL", originalURL);

            // 再導向登入頁
            response.sendRedirect("/member/login");
            return false;  // 中斷後續處理
        }

        // 已登入 → 直接放行
        return true;
    }

}

