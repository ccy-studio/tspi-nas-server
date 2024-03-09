package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.service.BucketsService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
