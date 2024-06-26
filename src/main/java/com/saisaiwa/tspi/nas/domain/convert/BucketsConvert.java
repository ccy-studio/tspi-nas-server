package com.saisaiwa.tspi.nas.domain.convert;

import com.saisaiwa.tspi.nas.domain.dto.BucketsExtDto;
import com.saisaiwa.tspi.nas.domain.dto.PoliciesRuleExtDto;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.domain.vo.BucketsAclInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsDetailVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

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

    BucketsDetailVo toBucketsDetailVo(Buckets buckets);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm")
    BucketsInfoVo toBucketsInfoVo(BucketsExtDto dto);

    List<BucketsInfoVo> toBucketsInfoVo(List<BucketsExtDto> dto);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm")
    BucketsAclInfoVo toBucketsAclInfoVo(PoliciesRuleExtDto extDto);

    List<BucketsAclInfoVo> toBucketsAclInfoVo(List<PoliciesRuleExtDto> extDto);
}
