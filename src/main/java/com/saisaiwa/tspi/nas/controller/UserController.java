package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.anno.SessionCheck;
import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.IdReq;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.UserPasswordReq;
import com.saisaiwa.tspi.nas.domain.req.UserQueryReq;
import com.saisaiwa.tspi.nas.domain.req.UserRegisterReq;
import com.saisaiwa.tspi.nas.domain.req.UserUpdateReq;
import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.UserListVo;
import com.saisaiwa.tspi.nas.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 用户管理
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 10:51
 * @Version：1.0
 */
@RestController
@RequestMapping("/sys/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 查询用户列表信息
     *
     * @param req
     * @return
     */
    @SessionCheck(ignore = true)
    @GetMapping("/list")
    public BaseResponse<PageBodyResponse<UserListVo>> getUserList(UserQueryReq req) {
        return BaseResponse.ok(userService.getUserList(req));
    }

    /**
     * 获取用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserInfoVo> getUserInfo(@NotNull Long id) {
        return BaseResponse.ok(userService.getUserInfo(id));
    }

    /**
     * 删除用户
     *
     * @param req
     */
    @PostMapping("/delete")
    public BaseResponse<Void> deleteUser(@RequestBody @Validated IdReq<Long> req) {
        userService.deleteUser(req.getId());
        return BaseResponse.ok();
    }

    /**
     * 更新用户信息
     *
     * @param req
     */
    @PostMapping("/update")
    public BaseResponse<Void> updateUser(@RequestBody @Validated UserUpdateReq req) {
        userService.updateUser(req);
        return BaseResponse.ok();
    }

    /**
     * 修改密码
     *
     * @param req
     */
    @PostMapping("/change-pwd")
    public BaseResponse<Void> changePassword(@RequestBody @Validated UserPasswordReq req) {
        userService.changePassword(req);
        return BaseResponse.ok();
    }

    /**
     * 注册用户
     *
     * @param req
     */
    @PostMapping("/register")
    public BaseResponse<Void> register(@RequestBody @Validated UserRegisterReq req) {
        userService.register(req);
        return BaseResponse.ok();
    }
}
