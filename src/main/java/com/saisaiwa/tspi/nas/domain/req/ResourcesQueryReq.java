package com.saisaiwa.tspi.nas.domain.req;

import com.saisaiwa.tspi.nas.common.bean.BasePageReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 9:30
 * @Version：1.0
 */
@Setter
@Getter
public class ResourcesQueryReq extends BasePageReq {

    private Long id;

    private String keyword;

    /**
     * 资源类型 0:FILE,1:SMB,2:FTP,3.WebDav
     */
    private Integer resType;

}
