package com.saisaiwa.tspi.nas.domain.vo;

import com.saisaiwa.tspi.nas.domain.dto.PoliciesRuleExtDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @description:
 * @date: 2024/03/11 14:53
 * @author: saisiawa
 **/
@Getter
@Setter
public class BucketsAclInfoVo extends PoliciesRuleExtDto {

    /**
     * 用户ACL列表
     */
    private List<String> acl;

    /**
     * 用户资源是否包含内
     */
    private Boolean isResContain;
}
