package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.entity.UserGroupBind;

import java.util.List;

/**
 * <p>
 * 用户组绑定关联表 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface UserGroupBindMapper extends BaseMapper<UserGroupBind> {

    void deleteByUserId(Long id);

    void deleteByUserGroupId(Long id);

    List<Long> selectUserGroupIdsByUserId(Long uid);
}
