package com.saisaiwa.tspi.nas.config;

import cn.hutool.core.util.StrUtil;
import com.saisaiwa.tspi.nas.common.anno.SessionCheck;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.service.SessionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/6 17:54
 * @Version：1.0
 */
@Component
public class SysSessionInterceptor implements HandlerInterceptor {

    @Resource
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest httpReq, HttpServletResponse response, Object handler) throws Exception {
        boolean ignoreSession = this.ignoreHandle(handler);
        String authorization = httpReq.getHeader("Authorization");
        if (StrUtil.isEmpty(authorization)) {
            // 需要登录态，但没有传Token
            if (!ignoreSession) {
                throw new BizException(RespCode.UNAUTHORIZED);
            }
        } else {
            sessionService.checkTokenAndGetSession(authorization);
        }
        return true;
    }

    private boolean ignoreHandle(Object handler) {
        HandlerMethod method = (HandlerMethod) handler;
        SessionCheck sessionCheck = method.getMethodAnnotation(SessionCheck.class);
        return (sessionCheck != null && sessionCheck.ignore());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SessionInfo.clear();
    }
}
