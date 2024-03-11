package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.dto.UserExtDto;
import com.saisaiwa.tspi.nas.domain.entity.Resources;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.req.UserQueryReq;

import java.util.List;

/**
 * <p>
 * 用户主表 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据账户查询用户信息
     *
     * @param account
     * @return
     */
    User selectByUserAccountUser(String account);

    /**
     * 根据AK查用户
     *
     * @param ak
     * @return
     */
    User selectByAccessKeyUser(String ak);

    /**
     * 查询用户列表信息
     *
     * @param query
     * @return
     */
    List<UserExtDto> selectUserExt(UserQueryReq query);

    /**
     * 查询获取这个用户所关联到的全部资源数据
     *
     * @param uid
     * @return
     */
    List<Resources> selectResAllByUserBinds(Long uid);
}
