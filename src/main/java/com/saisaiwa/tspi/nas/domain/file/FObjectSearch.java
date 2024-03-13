package com.saisaiwa.tspi.nas.domain.file;

import com.saisaiwa.tspi.nas.common.bean.BasePageReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @description:
 * @date: 2024/03/13 16:24
 * @author: saisiawa
 **/
@Setter
@Getter
public class FObjectSearch extends BasePageReq {

    /**
     * 桶ID
     */
    private Long bucketId;

    /**
     * 父Id
     */
    private Long parentId;

    /**
     * 名称搜索
     */
    private String searchName;

    /**
     * 跳转路径
     */
    private String gotoPath;

    /**
     * 文件类型
     */
    private List<String> fileType;

}
