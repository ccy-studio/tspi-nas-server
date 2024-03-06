package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.saisaiwa.tspi.nas.common.bean.TokenClaim;
import com.saisaiwa.tspi.nas.common.bean.TokenPair;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.config.SystemConfiguration;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.req.LoginReq;
import com.saisaiwa.tspi.nas.domain.vo.LoginRspVo;
import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;
import com.saisaiwa.tspi.nas.mapper.UserMapper;
import com.saisaiwa.tspi.nas.service.SessionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/6 17:56
 * @Version：1.0
 */
@Service
public class SessionServiceImpl implements SessionService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SystemConfiguration systemConfiguration;

    private Algorithm jwtAlgorithm;

    @PostConstruct
    public void init() {
        jwtAlgorithm = Algorithm.HMAC256(systemConfiguration.getAccessTokenSecurityKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * AccessToken 有效期
     */
    private long getAccessTokenTtl() {
        return systemConfiguration.getAccessTokenTtl() <= 0 ? TimeUnit.MINUTES.toSeconds(30) : systemConfiguration.getAccessTokenTtl();
    }

    /**
     * 登录
     *
     * @param req
     * @return
     */
    public LoginRspVo login(LoginReq req) {
        User user = userMapper.selectByUserAccountUser(req.getAccount());
        if (user == null) {
            throw new BizException(RespCode.LOGIN_FAIL);
        }
        String inputPwd = SecureUtil.md5(req.getPassword().trim() + user.getSalt());
        if (!inputPwd.equals(user.getUserPassword())) {
            throw new BizException(RespCode.LOGIN_FAIL);
        }
        Instant issuedAt = Instant.now();
        Instant expire = issuedAt.plusSeconds(this.getAccessTokenTtl());
        String accessToken = JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expire)
                .withClaim(TokenClaim.USER_ID, user.getId())
                .sign(jwtAlgorithm);
        TokenPair tokenPair = new TokenPair();
        tokenPair.setAccessToken(accessToken);
        tokenPair.setIat(issuedAt.getEpochSecond());
        tokenPair.setExpiresIn(expire.getEpochSecond());

        LoginRspVo rspVo = new LoginRspVo();
        rspVo.setTokenPair(tokenPair);
        rspVo.setUserInfo(BeanUtil.copyProperties(user, UserInfoVo.class));
        return rspVo;
    }

    /**
     * 生成密码与盐填充到User对象中
     *
     * @param user
     * @param originPwd
     */
    public void generatorPwd(User user, String originPwd) {
        user.setSalt(IdUtil.fastSimpleUUID());
        String pwd = SecureUtil.md5(originPwd.trim() + user.getSalt());
        user.setUserPassword(pwd);
    }

    public void register() {

    }

}
