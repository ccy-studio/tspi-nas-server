package com.saisaiwa.tspi.nas;

import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.service.impl.SessionServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TspiNasServerApplicationTests {


    @Resource
    private SessionServiceImpl sessionService;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUserAccount("admin");
        sessionService.generatorPwd(user, "admin");

    }

}
