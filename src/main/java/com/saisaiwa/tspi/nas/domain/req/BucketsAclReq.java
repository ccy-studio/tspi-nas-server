package com.saisaiwa.tspi.nas.domain.req;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author chen
 */
@Data
public class BucketsAclReq {

    /**
     * 存储桶id
     */
    @NotNull
    private Long bucketsId;

    /**
     * 用户ID
     */
    @NotNull
    private Long userId;

    /**
     * 权限描述符 get_obj,put_obj,del_obj,share_obj
     */
    @NotNull
    @NotEmpty
    private List<String> acl;

    /**
     * 动作:true允许,false拒绝
     */
    @NotNull
    private Boolean effect;

}
