package com.saisaiwa.tspi.nas.common.anno;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionCheck {
    /**
     * 是否忽略 登录态 校验
     */
    boolean ignore() default false;
}
