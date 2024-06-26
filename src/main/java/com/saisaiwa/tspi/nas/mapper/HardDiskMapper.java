package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.entity.HardDisk;
import com.saisaiwa.tspi.nas.domain.req.HardDiskQueryReq;

import java.util.List;

/**
 * <p>
 * 磁盘配置 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface HardDiskMapper extends BaseMapper<HardDisk> {

    List<HardDisk> selectTableList(HardDiskQueryReq req);

}
