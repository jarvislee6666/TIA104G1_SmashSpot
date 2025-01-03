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
	        .addPathPatterns("/member/basic-info","/client/memProductList","/client/addProduct")
	        .excludePathPatterns("/member/login");
    }
    
}
