package com.saisaiwa.tspi.nas.common.aop;

import com.saisaiwa.tspi.nas.common.anno.CheckBucketsAcl;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.enums.BucketsACLEnum;
import com.saisaiwa.tspi.nas.domain.vo.BucketsPermissionUserVo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @date: 2024/03/15 15:39
 * @author: saisiawa
 **/
@Component
@Aspect
public class BucketsACLAspect {


    @Before(value = "@annotation(com.saisaiwa.tspi.nas.common.anno.CheckBucketsAcl)")
    public void pointCut(JoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        CheckBucketsAcl acl = methodSignature.getMethod().getDeclaredAnnotation(CheckBucketsAcl.class);
        BucketsACLEnum[] value = acl.value();
        if (value != null && value.length != 0) {
            BucketsPermissionUserVo vo = SessionInfo.get().getBucketPermission();
            if (vo == null) {
                throw new BizException(RespCode.FILE_PERMISSION_DENIED);
            }
            for (BucketsACLEnum aclEnum : value) {
                switch (aclEnum) {
                    case GET_OBJ:
                        if (!vo.isRead()) {
                            throw new BizException(RespCode.FILE_PERMISSION_DENIED);
                        }
                        break;
                    case PUT_OBJ:
                        if (!vo.isWrite()) {
                            throw new BizException(RespCode.FILE_PERMISSION_DENIED);
                        }
                        break;
                    case DEL_OBJ:
                        if (!vo.isDelete()) {
                            throw new BizException(RespCode.FILE_PERMISSION_DENIED);
                        }
                        break;
                    case SHARE_OBJ:
                        if (!vo.isShare()) {
                            throw new BizException(RespCode.FILE_PERMISSION_DENIED);
                        }
                        break;
                    case SUPER_AUTH:
                        if (!vo.isManage()) {
                            throw new BizException(RespCode.FILE_PERMISSION_DENIED);
                        }
                        break;
                }
            }
        }
    }

}
