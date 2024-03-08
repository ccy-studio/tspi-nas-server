package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.req.HardDiskEditReq;
import com.saisaiwa.tspi.nas.domain.req.HardDiskQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.HardDiskInfoVo;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 9:38
 * @Version：1.0
 */
public interface HardDiskService {
    void saveOrUpdateHardDisk(HardDiskEditReq req);

    void deleteById(Long id);

    PageBodyResponse<HardDiskInfoVo> selectList(HardDiskQueryReq req);

    HardDiskInfoVo getDetailById(Long id);
}
