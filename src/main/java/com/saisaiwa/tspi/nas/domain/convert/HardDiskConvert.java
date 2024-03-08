package com.saisaiwa.tspi.nas.domain.convert;

import com.saisaiwa.tspi.nas.domain.entity.HardDisk;
import com.saisaiwa.tspi.nas.domain.req.HardDiskEditReq;
import com.saisaiwa.tspi.nas.domain.vo.HardDiskInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 17:44
 * @Version：1.0
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface HardDiskConvert {

    HardDiskConvert INSTANCE = Mappers.getMapper(HardDiskConvert.class);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm")
    HardDiskInfoVo toHardDiskInfoVo(HardDisk hardDisk);

    List<HardDiskInfoVo> toHardDiskInfoVo(List<HardDisk> hardDisk);

    HardDisk toHardDisk(HardDiskEditReq editReq);
}
