package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.entity.User;

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
     * @param ak
     * @return
     */
    User selectByAccessKeyUser(String ak);

}
