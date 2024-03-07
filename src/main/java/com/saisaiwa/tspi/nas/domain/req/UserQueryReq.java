package com.saisaiwa.tspi.nas.domain.req;

import com.saisaiwa.tspi.nas.common.bean.BasePageReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 10:55
 * @Version：1.0
 */
@Setter
@Getter
public class UserQueryReq extends BasePageReq {

    private Long id;

    /**
     * 用户组ID
     */
    private Long groupId;

    private String keyword;

}
