package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.ResourcesEditReq;
import com.saisaiwa.tspi.nas.domain.req.ResourcesQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.ResourcesInfoVo;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 17:42
 * @Version：1.0
 */
public interface ResourcesService {
    void saveOrUpdateResources(ResourcesEditReq req);

    void deleteById(Long id);

    PageBodyResponse<ResourcesInfoVo> selectList(ResourcesQueryReq req);

    ResourcesInfoVo getDetailById(Long id);
}
