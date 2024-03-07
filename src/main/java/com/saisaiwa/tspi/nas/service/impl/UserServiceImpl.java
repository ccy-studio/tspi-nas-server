package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.convert.UserConvert;
import com.saisaiwa.tspi.nas.domain.dto.UserExtDto;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.enums.UserEnum;
import com.saisaiwa.tspi.nas.domain.req.UserQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.UserListVo;
import com.saisaiwa.tspi.nas.mapper.FileObjectShareMapper;
import com.saisaiwa.tspi.nas.mapper.UserMapper;
import com.saisaiwa.tspi.nas.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Resource
    private UserConvert userConvert;

    private FileObjectShareMapper objectShareMapper;

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

    /**
     * 查询用户列表信息
     *
     * @param req
     * @return
     */
    @Override
    public PageBodyResponse<UserListVo> getUserList(UserQueryReq req) {
        List<UserExtDto> userExtList = userMapper.selectUserExt(req);
        return PageBodyResponse.convert(req, userConvert.toUserListVo(userExtList));
    }

    public void deleteUser(Long id) {
        if (id.intValue() == UserEnum.ADMIN.getCode()) {
            throw new BizException(RespCode.DATA_REFUSE);
        }
        userMapper.deleteById(id);
    }
}
