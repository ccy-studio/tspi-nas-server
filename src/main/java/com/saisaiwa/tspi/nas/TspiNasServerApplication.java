package com.saisaiwa.tspi.nas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class TspiNasServerApplication {

    public static void main(String[] args) {
        log.info("Start TSPI NAS SERVER...");
        SpringApplication.run(TspiNasServerApplication.class, args);
        log.info("====>>> Started TSPI NAS SERVER Successful!");
    }

}
