package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.BucketsInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsPermissionUserVo;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:14
 * @Version：1.0
 */
public interface BucketsService {
    void createBuckets(BucketsEditReq req);

    List<BucketsInfoVo> getBucketAll(BucketsQueryReq req);

    BucketsPermissionUserVo getPermissionByUser(Long bucketsId, Long uid);
}
