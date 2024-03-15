package com.saisaiwa.tspi.nas.common.anno;

import com.saisaiwa.tspi.nas.domain.enums.BucketsACLEnum;

import java.lang.annotation.*;

/**
 * @description:
 * @date: 2024/03/15 15:36
 * @author: saisiawa
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckBucketsAcl {

    /**
     * 判断是否具有权限
     *
     * @return BucketsACLEnum[]
     */
    BucketsACLEnum[] value() default {BucketsACLEnum.GET_OBJ};

}
