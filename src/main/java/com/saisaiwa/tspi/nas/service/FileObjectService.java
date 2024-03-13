package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.file.FObjectDelete;
import com.saisaiwa.tspi.nas.domain.file.FObjectSearch;
import com.saisaiwa.tspi.nas.domain.vo.FileObjectInfoVo;

/**
 * @description:
 * @date: 2024/03/13 15:55
 * @author: saisiawa
 **/
public interface FileObjectService {
    PageBodyResponse<FileObjectInfoVo> selectObjectAll(FObjectSearch search);

    void deleteObject(FObjectDelete dat);
}
