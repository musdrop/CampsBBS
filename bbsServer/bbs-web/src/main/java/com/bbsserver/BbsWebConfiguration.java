package com.bbsserver;

import com.bbsserver.common.CommonConfiguration;
import com.bbsserver.common.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({
        CommonConfiguration.class
})
public class BbsWebConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/login-code","/logout","/register","/test")
                .excludePathPatterns("/images/**");
    }
}
