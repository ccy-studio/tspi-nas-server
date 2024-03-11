package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.BucketsAclQueryReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsAclReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.BucketsAclInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsDetailVo;
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

    BucketsDetailVo getDetailBucketById(Long bid);

    void deleteBucketById(Long bid);

    void addOrUpdateAcl(BucketsAclReq req);

    void deleteAclById(Long id);

    PageBodyResponse<BucketsAclInfoVo> getAclByBucketsAll(BucketsAclQueryReq req);

    List<BucketsInfoVo> getBucketAll(BucketsQueryReq req);

    BucketsPermissionUserVo getPermissionByUser(Long bucketsId, Long uid);
}
