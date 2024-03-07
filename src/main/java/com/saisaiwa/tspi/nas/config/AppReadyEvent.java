package com.saisaiwa.tspi.nas.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 13:38
 * @Version：1.0
 */
@Component
public class AppReadyEvent {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStarted() {
        System.out.println("应用已经成功启动！");
        //TODO 这里进行初始化系统Mount挂载检查的操作
        System.out.println("检查磁盘挂载....");
    }

}
