package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 9:38
 * @Version：1.0
 */
public interface UserService {
    UserInfoVo getUserInfo(Long id);
}
