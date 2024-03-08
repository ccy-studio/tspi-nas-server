package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.IdReq;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.UserGroupEditReq;
import com.saisaiwa.tspi.nas.domain.req.UserGroupQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupDetailVo;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupListVo;
import com.saisaiwa.tspi.nas.service.UserGroupService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户组管理
 *
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 17:39
 * @Version：1.0
 */
@RestController
@RequestMapping("/sys/user-group")
@Validated
public class UserGroupController {

    @Resource
    private UserGroupService userGroupService;


    /**
     * 新建或者更新一个用户组的信息
     *
     * @param req
     */
    @PostMapping("/edit")
    public BaseResponse<Void> addOrUpdateUserGroup(@RequestBody @Validated UserGroupEditReq req) {
        userGroupService.addOrUpdateUserGroup(req);
        return BaseResponse.ok();
    }

    /**
     * 删除用户组根据用户组的ID
     *
     * @param id
     */
    @PostMapping("/delete")
    public BaseResponse<Void> deleteUserGroupById(@RequestBody @Validated IdReq<Long> req) {
        userGroupService.deleteUserGroupById(req.getId());
        return BaseResponse.ok();
    }

    /**
     * 列表查询
     *
     * @param req
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<PageBodyResponse<UserGroupListVo>> list(UserGroupQueryReq req) {
        return BaseResponse.ok(userGroupService.list(req));
    }

    /**
     * 查询用户组详情信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserGroupDetailVo> getDetailById(@NotNull Long id) {
        return BaseResponse.ok(userGroupService.getDetailById(id));
    }
}
