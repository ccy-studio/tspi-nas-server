package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.entity.ResourcesUserGroup;

/**
 * <p>
 * 存储资源关联用户组 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface ResourcesUserGroupMapper extends BaseMapper<ResourcesUserGroup> {

    void deleteByUserGroupId(Long id);

    int countByResId(Long id);
}
