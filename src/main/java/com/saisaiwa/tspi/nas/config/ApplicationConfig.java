package com.saisaiwa.tspi.nas.config;

import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
@EnableAsync
public class ApplicationConfig implements WebMvcConfigurer {
    @Resource
    private WebRequestLogAdapter webRequestLogAdapter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webRequestLogAdapter)
                .excludePathPatterns("/ok");
    }

    /**
     * 在应用程序启动时
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStarted() {
        System.out.println("应用已经成功启动！");
        //TODO 这里进行初始化系统Mount挂载检查的操作
        System.out.println("检查磁盘挂载....");
    }

}
