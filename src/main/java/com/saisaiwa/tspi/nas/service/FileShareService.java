package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.entity.FileObjectShare;
import com.saisaiwa.tspi.nas.domain.file.FObjectShare;
import com.saisaiwa.tspi.nas.domain.req.FileShareListQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.FileShareInfoVo;

/**
 * @description:
 * @date: 2024/03/18 11:35
 * @author: saisiawa
 **/
public interface FileShareService {
    FileObjectShare getByKey(String key);

    FileShareInfoVo createObjectShare(FObjectShare dat);

    void removeShareById(Long id);

    PageBodyResponse<FileShareInfoVo> getMyShareAll(FileShareListQueryReq req);
}
