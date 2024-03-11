package com.saisaiwa.tspi.nas.domain.dto;

import com.saisaiwa.tspi.nas.domain.entity.PoliciesRule;
import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @date: 2024/03/11 14:47
 * @author: saisiawa
 **/
@Getter
@Setter
public class PoliciesRuleExtDto extends PoliciesRule {

    /**
     * 用户的账户
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 桶绑定的资源id
     */
    private Long bucketsResId;

}
