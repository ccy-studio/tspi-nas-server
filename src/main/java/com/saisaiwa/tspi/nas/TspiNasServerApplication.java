package com.saisaiwa.tspi.nas;

import com.saisaiwa.tspi.nas.common.file.FileListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
@Slf4j
public class TspiNasServerApplication {

    public static void main(String[] args) {
//        log.info("Start TSPI NAS SERVER...");
//        SpringApplication.run(TspiNasServerApplication.class, args);
//        log.info("====>>> Started TSPI NAS SERVER Successful!");

        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        FileAlterationObserver observer = new FileAlterationObserver(new File("/Users/chen/Downloads/test"));
        monitor.addObserver(observer);
        observer.addListener(new FileListener(null));
        try {
            monitor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("End");
        while (true) ;
    }

}
