package com.saisaiwa.tspi.nas.domain.convert;

import com.saisaiwa.tspi.nas.domain.dto.UserExtDto;
import com.saisaiwa.tspi.nas.domain.vo.UserListVo;
import org.mapstruct.Mapper;
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


}
