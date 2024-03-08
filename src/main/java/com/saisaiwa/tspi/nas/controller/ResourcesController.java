package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.IdReq;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.ResourcesEditReq;
import com.saisaiwa.tspi.nas.domain.req.ResourcesQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.ResourcesInfoVo;
import com.saisaiwa.tspi.nas.service.ResourcesService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 资源管理
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 9:39
 * @Version：1.0
 */
@RestController
@RequestMapping("/sys/resources")
@Validated
public class ResourcesController {

    @Resource
    private ResourcesService resourcesService;

    /**
     * 新增或者修改资源
     *
     * @param req
     */
    @PostMapping("/edit")
    public BaseResponse<Void> saveOrUpdateResources(@RequestBody @Validated ResourcesEditReq req) {
        resourcesService.saveOrUpdateResources(req);
        return BaseResponse.ok();
    }

    /**
     * 删除资源根据ID
     *
     * @param id
     */
    @PostMapping("/delete")
    public BaseResponse<Void> deleteById(@RequestBody @Validated IdReq<Long> req) {
        resourcesService.deleteById(req.getId());
        return BaseResponse.ok();
    }

    /**
     * 查询资源列表数据
     *
     * @param req
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<PageBodyResponse<ResourcesInfoVo>> selectList(ResourcesQueryReq req) {
        return BaseResponse.ok(resourcesService.selectList(req));
    }

    /**
     * 查询资源详情根据ID
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ResourcesInfoVo> getDetailById(@NotNull Long id) {
        return BaseResponse.ok(resourcesService.getDetailById(id));
    }
}
