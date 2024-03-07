package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.anno.SessionCheck;
import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.UserQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.UserListVo;
import com.saisaiwa.tspi.nas.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
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
}
