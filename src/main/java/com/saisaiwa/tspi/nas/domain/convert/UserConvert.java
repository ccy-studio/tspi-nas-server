package com.saisaiwa.tspi.nas.domain.convert;

import com.saisaiwa.tspi.nas.domain.dto.UserExtDto;
import com.saisaiwa.tspi.nas.domain.dto.UserGroupExtDto;
import com.saisaiwa.tspi.nas.domain.entity.Resources;
import com.saisaiwa.tspi.nas.domain.entity.UserGroup;
import com.saisaiwa.tspi.nas.domain.req.UserGroupEditReq;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupDetailVo;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupListVo;
import com.saisaiwa.tspi.nas.domain.vo.UserListVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 11:35
 * @Version：1.0
 */
@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConvert {

    UserListVo toUserListVo(UserExtDto userExtDto);

    List<UserListVo> toUserListVo(List<UserExtDto> userExtDto);

    UserGroup toUserGroup(UserGroupEditReq req);

    List<UserGroupListVo> toUserGroupListVo(List<UserGroupExtDto> dto);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm")
    UserGroupDetailVo toUserGroupDetailVo(UserGroupExtDto dto);

    UserGroupDetailVo.ResourceItem toUserGroupDetailRes(Resources resource);

    List<UserGroupDetailVo.ResourceItem> toUserGroupDetailRes(List<Resources> resource);
}
