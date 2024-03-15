package com.saisaiwa.tspi.nas.config;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.vo.BucketsPermissionUserVo;
import com.saisaiwa.tspi.nas.service.BucketsService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @description:
 * @date: 2024/03/15 15:45
 * @author: saisiawa
 **/
@Component
public class FileObjectInterceptor implements HandlerInterceptor {

    private static final String H_TYPE = "X-AUTH-TYPE";
    private static final String H_TYPE_TOKEN = "token";
    private static final String H_TYPE_SIGN = "sign";


    private static final String H_BK = "X-BK";

    @Resource
    private SysSessionInterceptor sessionInterceptor;

    @Resource
    private BucketsService bucketsService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String type = getParamByHeaderOrParameter(request, H_TYPE);
        String bkId = getParamByHeaderOrParameter(request, H_BK);
        if (StrUtil.isBlank(type)) {
            throw new BizException(RespCode.FILE_PERMISSION_DENIED);
        }
        if (type.equalsIgnoreCase(H_TYPE_TOKEN)) {
            sessionInterceptor.preHandle(request, response, handler);
        } else if (type.equalsIgnoreCase(H_TYPE_SIGN)) {
            //签名校验
            //暂时不做
        } else {
            throw new BizException(RespCode.FILE_PERMISSION_DENIED);
        }
        if (StrUtil.isNotBlank(bkId) && NumberUtil.isNumber(bkId)) {
            long id = NumberUtil.parseLong(bkId);
            BucketsPermissionUserVo permissionByUser = bucketsService.getPermissionByUser(id, SessionInfo.get().getUid());
            if (permissionByUser == null) {
                throw new BizException(RespCode.FILE_PERMISSION_DENIED);
            }
            permissionByUser.setBucketsId(id);
            SessionInfo.get().setBucketPermission(permissionByUser);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SessionInfo.clear();
    }

    public String getParamByHeaderOrParameter(HttpServletRequest request, String key) {
        String val = request.getHeader(key);
        if (StrUtil.isBlankIfStr(val)) {
            val = request.getParameter(key);
        }
        return val;
    }
}
