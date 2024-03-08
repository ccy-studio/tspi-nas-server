package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:14
 * @Version：1.0
 */
public interface BucketsService {
    void createBuckets(BucketsEditReq req);
}
