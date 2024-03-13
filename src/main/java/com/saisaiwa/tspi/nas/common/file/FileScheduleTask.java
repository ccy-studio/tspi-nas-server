package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.saisaiwa.tspi.nas.config.SystemConfiguration;
import com.saisaiwa.tspi.nas.domain.entity.FileBlockRecords;
import com.saisaiwa.tspi.nas.mapper.FileBlockRecordsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @date: 2024/03/13 14:25
 * @author: saisiawa
 **/
@Component
@Slf4j
public class FileScheduleTask {

    @Resource
    private FileNativeService fileNativeService;

    @Resource
    private FileBlockRecordsMapper blockRecordsMapper;

    @Resource
    private SystemConfiguration configuration;


    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void taskDeleteBlock() {
        log.info("执行Task定时任务：删除过期未使用Block临时文件");
        List<FileBlockRecords> records = blockRecordsMapper.selectList(null);
        List<FileBlockRecords> delList = records.parallelStream()
                .filter(v -> {
                    Duration duration = LocalDateTimeUtil.between(v.getCreateTime(), LocalDateTime.now());
                    return duration.getSeconds() >= configuration.getBlockExpirationTime();
                }).toList();
        log.info("删除过期的block文件数量：{}", delList.size());
        if (delList.isEmpty()) {
            return;
        }
        delList.forEach(fileNativeService::cleanBlockTempFiles);
        blockRecordsMapper.deleteBatchIds(delList.stream().map(FileBlockRecords::getId).toList());
    }

}
