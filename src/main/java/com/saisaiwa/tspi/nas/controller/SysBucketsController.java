package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.IdReq;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.req.BucketsAclQueryReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsAclReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.BucketsAclInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsDetailVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsPermissionUserVo;
import com.saisaiwa.tspi.nas.service.BucketsService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 存储桶管理
 *
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/9 16:53
 * @Version：1.0
 */
@RestController
@RequestMapping("/sys/buckets")
@Validated
public class SysBucketsController {

    @Resource
    private BucketsService bucketsService;

    /**
     * 创建或者修改存储桶
     *
     * @param req
     */
    @PostMapping("/edit")
    public BaseResponse<Void> createBuckets(@RequestBody @Validated BucketsEditReq req) {
        bucketsService.createBuckets(req);
        return BaseResponse.ok();
    }


    /**
     * 根据ID获取详细的信息
     *
     * @param bid
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<BucketsDetailVo> getDetailBucketById(@NotNull Long bid) {
        return BaseResponse.ok(bucketsService.getDetailBucketById(bid));
    }

    /**
     * 删除一个存储桶根据ID
     *
     * @param req bid
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Void> deleteBucketById(@RequestBody @Validated IdReq<Long> req) {
        bucketsService.deleteBucketById(req.getId());
        return BaseResponse.ok();
    }


    /**
     * 添加一个ACL权限
     *
     * @param req
     */
    @PostMapping("/acl/edit")
    public BaseResponse<Void> addAcl(@RequestBody @Validated BucketsAclReq req) {
        checkAclAction(req.getBucketsId());
        bucketsService.addOrUpdateAcl(req);
        return BaseResponse.ok();
    }

    /**
     * 根据ACL的ID删除此ACL
     *
     * @param id
     */
    @PostMapping("/acl/delete")
    public BaseResponse<Void> deleteAclById(@RequestBody @Validated IdReq<Long> req) {
        bucketsService.deleteAclById(req.getId());
        return BaseResponse.ok();
    }

    /**
     * 获取桶的ACL列表数据
     *
     * @param req
     * @return
     */
    @GetMapping("/acl/list")
    public BaseResponse<PageBodyResponse<BucketsAclInfoVo>> getAclByBucketsAll(BucketsAclQueryReq req) {
        checkAclAction(req.getBid());
        return BaseResponse.ok(bucketsService.getAclByBucketsAll(req));
    }


    /**
     * 查询此用户可见的存储桶
     *
     * @param req
     * @return
     */
    @GetMapping("list")
    public BaseResponse<List<BucketsInfoVo>> getBucketAll(BucketsQueryReq req) {
        return BaseResponse.ok(bucketsService.getBucketAll(req, true));
    }


    /**
     * 尝试重新恢复存储桶
     *
     * @param req -ID
     * @return
     */
    @PostMapping("tryRecovery")
    public BaseResponse<Boolean> tryRecoveryBucket(@RequestBody @Validated IdReq<Long> req) {
        return BaseResponse.ok(bucketsService.tryRecoveryBucket(req.getId()));
    }

    /**
     * 检查是否具有管理桶的权限
     *
     * @param bid
     */
    private void checkAclAction(Long bid) {
        if (bid == null) {
            throw new BizException(RespCode.BAD_REQUEST);
        }
        BucketsPermissionUserVo acl = bucketsService.getPermissionByUser(bid, SessionInfo.get().getUid());
        if (!acl.isManage()) {
            throw new BizException(RespCode.ACL_PERMISSION_DENIED);
        }
    }
}
