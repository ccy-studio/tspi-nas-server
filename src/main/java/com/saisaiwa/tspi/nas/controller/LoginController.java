package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.anno.SessionCheck;
import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.domain.req.LoginReq;
import com.saisaiwa.tspi.nas.domain.vo.LoginRspVo;
import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;
import com.saisaiwa.tspi.nas.service.SessionService;
import com.saisaiwa.tspi.nas.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 9:35
 * @Version：1.0
 */
@RestController
@RequestMapping("/sys")
@Validated
public class LoginController {

    @Resource
    private SessionService sessionService;

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public BaseResponse<LoginRspVo> login(@Validated @RequestBody LoginReq req) {
        return BaseResponse.ok(sessionService.login(req));
    }

    /**
     * 获取当前用户登录的信息
     *
     * @return
     */
    @GetMapping("/current-user")
    public BaseResponse<UserInfoVo> getCurrentUserInfo() {
        UserInfoVo userInfo = userService.getUserInfo(SessionInfo.get().getUid());
        return Optional.ofNullable(userInfo)
                .map(BaseResponse::ok)
                .orElse(BaseResponse.fail());
    }


}
