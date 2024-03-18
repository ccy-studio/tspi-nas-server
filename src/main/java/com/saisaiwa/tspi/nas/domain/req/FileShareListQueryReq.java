package com.saisaiwa.tspi.nas.domain.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saisaiwa.tspi.nas.common.bean.BasePageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2024/03/18 14:22
 * @author: saisiawa
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FileShareListQueryReq extends BasePageReq {

    private String fileName;

    @JsonIgnore
    private Long bucketsId;

    @JsonIgnore
    private Long uid;
}
