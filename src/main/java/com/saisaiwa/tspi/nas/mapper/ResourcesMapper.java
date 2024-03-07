package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.entity.Resources;

import java.util.List;

/**
 * <p>
 * 存储资源 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface ResourcesMapper extends BaseMapper<Resources> {

    /**
     * 根据用户组的ID查询资源列表
     *
     * @param id
     * @return
     */
    List<Resources> selectAllByUserGroupId(Long id);

}
