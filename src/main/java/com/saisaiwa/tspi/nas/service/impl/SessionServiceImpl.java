package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
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
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(rollbackFor = Exception.class)
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
    @Override
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
     * Token检查解析
     *
     * @param token
     * @return
     */
    @Override
    public void checkTokenAndGetSession(String token) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.require(jwtAlgorithm).build().verify(token);
        } catch (TokenExpiredException e) {
            throw new BizException(RespCode.SESSION_TIMEOUT);
        } catch (JWTVerificationException e) {
            throw new BizException(RespCode.INVALID_TOKEN);
        }

        if (decodedJWT == null) {
            throw new BizException(RespCode.INVALID_TOKEN);
        }
        Claim userId = decodedJWT.getClaim(TokenClaim.USER_ID);
        if (userId == null || userId.isNull()) {
            throw new BizException(RespCode.INVALID_TOKEN);
        }
        Long uid = userId.asLong();
        User user = userMapper.selectById(uid);
        if (user == null || user.getIsDelete() != 0) {
            throw new BizException(RespCode.INVALID_TOKEN);
        }
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUser(user);
        sessionInfo.setUid(uid);
        sessionInfo.setAk(user.getAccessKey());
        sessionInfo.setSk(user.getSecretKey());
        SessionInfo.set(sessionInfo);
    }

}
