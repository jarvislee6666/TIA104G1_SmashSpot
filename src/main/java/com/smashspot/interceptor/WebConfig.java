package com.smashspot.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdmLoginInterceptor())
               .addPathPatterns("/adm/**")
               .excludePathPatterns("/adm/login");
        
        registry.addInterceptor(new LoginInterceptor())

	        .addPathPatterns("/member/basic-info","/client/memProductList",
	        				 "/client/addProduct","/reservation/week","/client/orders/DPstep1/{proid}",
	        				 "member/appointment-records", "/court-order/**","/client/bid/**", 
	        				 "/chat/Adm/{senderId}","/mem/websocket/chat/{memname}","/client/favorite/list")
	        .excludePathPatterns("/member/login");

    }
    
}