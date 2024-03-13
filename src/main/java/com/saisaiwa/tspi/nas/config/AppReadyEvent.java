package com.saisaiwa.tspi.nas.config;

import com.saisaiwa.tspi.nas.common.file.FileLocalScanService;
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
    private FileLocalScanService fileLocalScanService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStarted() {
        log.info("1. 初始化磁盘挂载…………");
        //todo
        log.info(" -磁盘挂载初始化完成！");

        log.info("2. 开始扫描差异文件并同步…………");
        fileLocalScanService.scanAllBuckets();
        log.info(" -同步完成！");

        log.info("3. 启动磁盘文件监听器…………");
        //启动文件监听
        fileLocalScanService.initListener();
        log.info(" -文件监听开启成功");


        log.info("******》》》系统启动成功《《《******");
    }

}
