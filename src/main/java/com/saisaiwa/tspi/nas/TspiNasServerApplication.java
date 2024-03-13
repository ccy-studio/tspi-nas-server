package com.saisaiwa.tspi.nas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author chen
 */
@SpringBootApplication
@EnableScheduling
public class TspiNasServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TspiNasServerApplication.class, args);
    }

}
