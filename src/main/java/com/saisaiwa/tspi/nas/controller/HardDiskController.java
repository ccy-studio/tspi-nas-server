package com.saisaiwa.tspi.nas.controller;

import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.IdReq;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.HardDiskEditReq;
import com.saisaiwa.tspi.nas.domain.req.HardDiskQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.HardDiskInfoVo;
import com.saisaiwa.tspi.nas.service.HardDiskService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 磁盘管理
 *
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 9:58
 * @Version：1.0
 */
@RestController
@RequestMapping("/sys/hard-disk")
@Validated
public class HardDiskController {

    @Resource
    private HardDiskService hardDiskService;

    /**
     * 新增或者更新一个磁盘配置项
     *
     * @param req
     */
    @PostMapping("/edit")
    public BaseResponse<Void> saveOrUpdateHardDisk(@RequestBody @Validated HardDiskEditReq req) {
        hardDiskService.saveOrUpdateHardDisk(req);
        return BaseResponse.ok();
    }

    /**
     * 删除一条磁盘配置
     *
     * @param id
     */
    @PostMapping("/delete")
    public BaseResponse<Void> deleteById(@RequestBody @Validated IdReq<Long> req) {
        hardDiskService.deleteById(req.getId());
        return BaseResponse.ok();
    }

    /**
     * 查询磁盘列表
     *
     * @param req
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<PageBodyResponse<HardDiskInfoVo>> selectList(HardDiskQueryReq req) {
        return BaseResponse.ok(hardDiskService.selectList(req));
    }

    /**
     * 获取磁盘详情内容
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<HardDiskInfoVo> getDetailById(@NotNull Long id) {
        return BaseResponse.ok(hardDiskService.getDetailById(id));
    }
}
