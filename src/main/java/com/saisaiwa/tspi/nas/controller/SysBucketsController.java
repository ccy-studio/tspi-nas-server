package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.BucketsInfoVo;
import com.saisaiwa.tspi.nas.service.BucketsService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
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
     * 查询此用户可见的存储桶
     *
     * @param req
     * @return
     */
    @GetMapping("list")
    public BaseResponse<List<BucketsInfoVo>> getBucketAll(BucketsQueryReq req) {
        return BaseResponse.ok(bucketsService.getBucketAll(req));
    }
}
