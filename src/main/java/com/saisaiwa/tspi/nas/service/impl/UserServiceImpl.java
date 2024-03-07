package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;
import com.saisaiwa.tspi.nas.mapper.UserMapper;
import com.saisaiwa.tspi.nas.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 9:38
 * @Version：1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 获取用户信息
     *
     * @param id
     * @return
     */
    @Override
    public UserInfoVo getUserInfo(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return BeanUtil.copyProperties(user, UserInfoVo.class);
    }
}
