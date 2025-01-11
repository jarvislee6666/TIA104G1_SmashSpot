package com.smashspot.websocketchat.chat;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.smashspot.interceptor.AdmLoginInterceptor;
import com.smashspot.interceptor.LoginInterceptor;

@Configuration
public class WebConfig2 implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdmLoginInterceptor admLoginInterceptor;

    public WebConfig2(LoginInterceptor loginInterceptor, AdmLoginInterceptor admLoginInterceptor) {
        this.loginInterceptor = loginInterceptor;
        this.admLoginInterceptor = admLoginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/messages/**", "/chat");
        registry.addInterceptor(admLoginInterceptor).addPathPatterns("/admin/**");
    }
}