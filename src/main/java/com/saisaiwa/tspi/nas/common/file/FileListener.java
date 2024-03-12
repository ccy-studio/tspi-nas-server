package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.io.FileUtil;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * @description:
 * @date: 2024/03/12 16:02
 * @author: saisiawa
 **/
@Slf4j
@Getter
public class FileListener implements FileAlterationListener {

    private final Buckets buckets;

    public FileListener(Buckets buckets) {
        this.buckets = buckets;
    }

    @Override
    public void onDirectoryChange(File file) {
        printFileInfo(file,"onDirectoryChange");
    }

    @Override
    public void onDirectoryCreate(File file) {
        printFileInfo(file,"onDirectoryCreate");
    }

    @Override
    public void onDirectoryDelete(File file) {
        printFileInfo(file,"onDirectoryDelete");
    }

    @Override
    public void onFileChange(File file) {
        printFileInfo(file,"onFileChange");
    }

    @Override
    public void onFileCreate(File file) {
        printFileInfo(file,"onFileCreate");
    }

    @Override
    public void onFileDelete(File file) {
        printFileInfo(file,"onFileDelete");
    }

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {

    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {

    }

    private void printFileInfo(File file, String msg) {
        log.info("{} FileInfo:{} {} {}", msg, file.getName(), FileUtil.size(file, true), file.getAbsolutePath());
    }
}
