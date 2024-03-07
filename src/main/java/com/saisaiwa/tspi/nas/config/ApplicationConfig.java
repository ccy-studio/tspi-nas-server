package com.saisaiwa.tspi.nas.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
@EnableAsync
@Order(2)
public class ApplicationConfig implements WebMvcConfigurer {
    @Resource
    private WebRequestLogAdapter webRequestLogAdapter;

    @Resource
    private SysSessionInterceptor sysSessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webRequestLogAdapter)
                .excludePathPatterns("/ok");
        registry.addInterceptor(sysSessionInterceptor)
                .excludePathPatterns("/ok")
                .excludePathPatterns("/sys/login")
                .excludePathPatterns("/ntf");
    }
}
