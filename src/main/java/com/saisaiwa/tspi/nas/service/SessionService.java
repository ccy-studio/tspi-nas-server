package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.domain.req.LoginReq;
import com.saisaiwa.tspi.nas.domain.vo.LoginRspVo;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/6 17:56
 * @Version：1.0
 */
public interface SessionService {
    LoginRspVo login(LoginReq req);

    void checkTokenAndGetSession(String token);

}
