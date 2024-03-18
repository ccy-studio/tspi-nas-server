package com.saisaiwa.tspi.nas.domain.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2024/03/13 16:45
 * @author: saisiawa
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FObjectShare extends FBaseEntity {

    /**
     * 过期时间,为空永久
     */
    private String expirationTime;


    /**
     * 访问密码
     */
    private String accessPassword;


    /**
     * 是否是直链
     */
    private boolean symlink;

}
