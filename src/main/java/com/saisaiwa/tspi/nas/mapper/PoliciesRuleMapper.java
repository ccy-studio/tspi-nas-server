package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.dto.PoliciesRuleExtDto;
import com.saisaiwa.tspi.nas.domain.entity.PoliciesRule;
import com.saisaiwa.tspi.nas.domain.req.BucketsAclQueryReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 策略规则 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface PoliciesRuleMapper extends BaseMapper<PoliciesRule> {

    /**
     * 查询ACL的权限根据用户的id和桶id
     * @param uid
     * @param bid
     * @return
     */
    PoliciesRule selectByUserIdAndBucketsId(@Param("uid") Long uid, @Param("bid") Long bid);


    /**
     * 根据桶id查询所有的acl
     * @param req
     * @return
     */
    List<PoliciesRuleExtDto> selectExtAll(BucketsAclQueryReq req);
}
