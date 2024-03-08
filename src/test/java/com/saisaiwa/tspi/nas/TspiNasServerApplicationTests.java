package com.saisaiwa.tspi.nas;

import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("dev")
class TspiNasServerApplicationTests {


    @Resource
    private UserServiceImpl userServiceImpl;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUserAccount("admin");
        userServiceImpl.generatorPwd(user, "21232f297a57a5a743894a0e4a801fc3");
        System.out.printf("Salt:%s, Pwd:%s\n",user.getSalt(),user.getUserPassword());
    }

}
