package com.saisaiwa.tspi.nas.domain.convert;

import com.saisaiwa.tspi.nas.domain.entity.Resources;
import com.saisaiwa.tspi.nas.domain.req.ResourcesEditReq;
import com.saisaiwa.tspi.nas.domain.vo.ResourcesInfoVo;
import org.mapstruct.Mapper;
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
public interface ResourcesConvert {

    ResourcesConvert INSTANCE = Mappers.getMapper(ResourcesConvert.class);

    Resources toResources(ResourcesEditReq req);

    ResourcesInfoVo toResourcesInfoVo(Resources resource);

    List<ResourcesInfoVo> toResourcesInfoVo(List<Resources> resource);
}
