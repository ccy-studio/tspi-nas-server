package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.UserQueryReq;
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
}
