package com.saisaiwa.tspi.nas.domain.req;

import com.saisaiwa.tspi.nas.common.bean.BasePageReq;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @description
 * @date 2024/03/11 14:43
 * @author saisiawa
 **/
@Setter
@Getter
public class BucketsAclQueryReq extends BasePageReq {

    @NotNull
    private Long bid;

    /**
     * 关键字查询
     */
    private String keyword;

    /**
     * 指定Action
     */
    private String action;
}
