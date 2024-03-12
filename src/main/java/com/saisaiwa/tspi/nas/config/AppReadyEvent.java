package com.saisaiwa.tspi.nas.config;

import com.saisaiwa.tspi.nas.common.file.FileLocalScanTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AppReadyEvent {

    @Resource
    private FileLocalScanTask fileLocalScanTask;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStarted() {
        log.info("初始化\n1. 扫描本地文件改动并同步...");
        fileLocalScanTask.scanAllBuckets();
        log.info("扫描同步成功！\n2. 开启文件监听...");
        //启动文件监听
        fileLocalScanTask.initListener();
        log.info("文件监听开启成功");
    }

}
