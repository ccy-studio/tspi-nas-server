package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.req.UserPasswordReq;
import com.saisaiwa.tspi.nas.domain.req.UserQueryReq;
import com.saisaiwa.tspi.nas.domain.req.UserRegisterReq;
import com.saisaiwa.tspi.nas.domain.req.UserUpdateReq;
import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.UserListVo;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 9:38
 * @Version：1.0
 */
public interface UserService {
    UserInfoVo getUserInfo(Long id);

    PageBodyResponse<UserListVo> getUserList(UserQueryReq req);

    void deleteUser(Long id);

    void updateUser(UserUpdateReq req);

    String getSk(String account);

    User getUserByAccount(String account);

    void changePassword(UserPasswordReq req);

    void register(UserRegisterReq req);

    void resetPassword(Long uid);
}
