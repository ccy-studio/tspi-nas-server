package com.saisaiwa.tspi.nas.common.bean;

import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.vo.BucketsPermissionUserVo;
import lombok.Data;

/**
 * @Author: Saisaiwa
 * @Date: 2024/03/06/ $
 * @Description:
 */
@Data
public class SessionInfo {

    private User user;

    private Long uid;

    private String ak;

    private String sk;

    /**
     * 用户当前对桶的权限ACL
     */
    private BucketsPermissionUserVo bucketPermission;

    private static final ThreadLocal<SessionInfo> SESSION_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public static SessionInfo get() {
        SessionInfo sessionInfo = SESSION_INFO_THREAD_LOCAL.get();
        if (sessionInfo == null) {
            throw new BizException(RespCode.SESSION_TIMEOUT);
        }
        return sessionInfo;
    }

    public static SessionInfo getAndNull() {
        return SESSION_INFO_THREAD_LOCAL.get();
    }

    public static void set(SessionInfo sessionInfo) {
        SESSION_INFO_THREAD_LOCAL.set(sessionInfo);
    }

    public static void clear() {
        SESSION_INFO_THREAD_LOCAL.remove();
    }
}
