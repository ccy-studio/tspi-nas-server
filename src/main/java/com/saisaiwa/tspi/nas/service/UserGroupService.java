package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.UserGroupEditReq;
import com.saisaiwa.tspi.nas.domain.req.UserGroupQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupDetailVo;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupListVo;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 15:38
 * @Version：1.0
 */
public interface UserGroupService {
    void addOrUpdateUserGroup(UserGroupEditReq req);

    void deleteUserGroupById(Long id);

    PageBodyResponse<UserGroupListVo> list(UserGroupQueryReq req);

    UserGroupDetailVo getDetailById(Long id);
}
