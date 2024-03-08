package com.saisaiwa.tspi.nas.domain.dto;

import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 11:38
 * @Version：1.0
 */
@Getter
@Setter
public class BucketsPremissDto extends Buckets {

    /**
     * 绑定用户ID
     */
    private Long userId;

    /**
     * 动作:true允许,false拒绝
     */
    private Boolean effect;

    /**
     * 操作:get_obj,put_obj,del_obj,share_obj,super
     */
    private String action;

}
