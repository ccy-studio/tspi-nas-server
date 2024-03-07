package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.domain.req.ResourcesEditReq;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 17:42
 * @Version：1.0
 */
public interface ResourcesService {
    void saveOrUpdateResources(ResourcesEditReq req);
}
