package com.saisaiwa.tspi.nas.domain.convert;

import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:22
 * @Version：1.0
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BucketsConvert {
    BucketsConvert INSTANCE = Mappers.getMapper(BucketsConvert.class);

    Buckets toBuckets(BucketsEditReq req);
}
