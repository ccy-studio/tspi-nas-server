package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.dto.UserGroupExtDto;
import com.saisaiwa.tspi.nas.domain.entity.UserGroup;
import com.saisaiwa.tspi.nas.domain.req.UserGroupQueryReq;

import java.util.List;

/**
 * <p>
 * 用户组 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface UserGroupMapper extends BaseMapper<UserGroup> {

    List<UserGroupExtDto> selectExtInfoList(UserGroupQueryReq req);

}
