package com.saisaiwa.tspi.nas.service.impl;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.convert.HardDiskConvert;
import com.saisaiwa.tspi.nas.domain.entity.HardDisk;
import com.saisaiwa.tspi.nas.domain.req.HardDiskEditReq;
import com.saisaiwa.tspi.nas.domain.req.HardDiskQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.HardDiskInfoVo;
import com.saisaiwa.tspi.nas.mapper.HardDiskMapper;
import com.saisaiwa.tspi.nas.service.HardDiskService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 9:38
 * @Version：1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HardDiskServiceImpl implements HardDiskService {

    @Resource
    private HardDiskMapper hardDiskMapper;

    /**
     * 新增或者更新一个磁盘配置项
     *
     * @param req
     */
    @Override
    public void saveOrUpdateHardDisk(HardDiskEditReq req) {
        HardDisk hardDisk = HardDiskConvert.INSTANCE.toHardDisk(req);
        if (req.getId() == null) {
            //insert
            hardDisk.setCreateUser(SessionInfo.get().getUid());
            hardDiskMapper.insert(hardDisk);
        } else {
            //update
            hardDisk.setUpdateTime(LocalDateTime.now());
            hardDisk.setUpdateUser(SessionInfo.get().getUid());
            hardDiskMapper.updateById(hardDisk);
        }
    }

    /**
     * 删除一条磁盘配置
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        if (hardDiskMapper.deleteById(id) <= 0) {
            throw new BizException(RespCode.PROMPT);
        }
    }

    /**
     * 查询磁盘列表
     *
     * @param req
     * @return
     */
    @Override
    public PageBodyResponse<HardDiskInfoVo> selectList(HardDiskQueryReq req) {
        List<HardDisk> hardDisks = hardDiskMapper.selectTableList(req);
        return PageBodyResponse.convert(req, HardDiskConvert.INSTANCE.toHardDiskInfoVo(hardDisks));
    }


    /**
     * 获取磁盘详情内容
     *
     * @param id
     * @return
     */
    @Override
    public HardDiskInfoVo getDetailById(Long id) {
        HardDiskQueryReq req = new HardDiskQueryReq();
        req.setId(id);
        List<HardDisk> hardDisks = hardDiskMapper.selectTableList(req);
        if (hardDisks.isEmpty()) {
            return null;
        }
        return HardDiskConvert.INSTANCE.toHardDiskInfoVo(hardDisks.get(0));
    }
}
