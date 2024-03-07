package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.convert.UserConvert;
import com.saisaiwa.tspi.nas.domain.dto.UserExtDto;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.entity.UserGroupBind;
import com.saisaiwa.tspi.nas.domain.enums.UserEnum;
import com.saisaiwa.tspi.nas.domain.req.UserPasswordReq;
import com.saisaiwa.tspi.nas.domain.req.UserQueryReq;
import com.saisaiwa.tspi.nas.domain.req.UserRegisterReq;
import com.saisaiwa.tspi.nas.domain.req.UserUpdateReq;
import com.saisaiwa.tspi.nas.domain.vo.UserInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.UserListVo;
import com.saisaiwa.tspi.nas.mapper.FileObjectShareMapper;
import com.saisaiwa.tspi.nas.mapper.UserGroupBindMapper;
import com.saisaiwa.tspi.nas.mapper.UserMapper;
import com.saisaiwa.tspi.nas.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Resource
    private FileObjectShareMapper objectShareMapper;

    @Resource
    private UserGroupBindMapper userGroupBindMapper;

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

    /**
     * 删除用户
     *
     * @param id
     */
    @Override
    public void deleteUser(Long id) {
        if (id.intValue() == UserEnum.ADMIN.getCode()) {
            throw new BizException(RespCode.DATA_REFUSE);
        }
        userMapper.deleteById(id);
        //删除此用户分享的文件
        objectShareMapper.deleteByCreateUser(id);
    }

    /**
     * 更新用户信息
     *
     * @param req
     */
    @Override
    public void updateUser(UserUpdateReq req) {
        User user = userMapper.selectById(req.getId());
        Assert.notNull(user);
        BeanUtil.copyProperties(req, user);
        userGroupBindMapper.deleteByUserId(req.getId());
        req.getUserGroupIds().stream().distinct().forEach(v -> {
            UserGroupBind bind = new UserGroupBind();
            bind.setUserId(user.getId());
            bind.setUserGroupId(v);
            if (userGroupBindMapper.insert(bind) <= 0) {
                throw new BizException(RespCode.ERROR);
            }
        });
    }

    /**
     * 修改密码
     *
     * @param req
     */
    @Override
    public void changePassword(UserPasswordReq req) {
        User user = userMapper.selectById(req.getId());
        Assert.notNull(user);
        //判断密码是否正确
        String pwd = SecureUtil.md5(req.getOldPwd().trim() + user.getSalt());
        if (!pwd.equals(user.getUserPassword())) {
            throw new BizException("原密码错误");
        }
        generatorPwd(user, req.getNewPwd());
        userMapper.updateById(user);
    }

    /**
     * 注册用户
     *
     * @param req
     */
    @Override
    public void register(UserRegisterReq req) {
        if (userMapper.selectByUserAccountUser(req.getUserAccount()) != null) {
            throw new BizException("此账号已存在");
        }
        User user = BeanUtil.copyProperties(req, User.class);
        generatorPwd(user, req.getPassword());
        String ak = StrUtil.sub(IdUtil.fastSimpleUUID(), 0, 20);
        String sk = StrUtil.sub(IdUtil.fastSimpleUUID() + IdUtil.fastSimpleUUID(), 0, 40);
        while (userMapper.selectByAccessKeyUser(ak) != null) {
            ak = StrUtil.sub(IdUtil.fastSimpleUUID(), 0, 20);
        }
        user.setAccessKey(ak);
        user.setSecretKey(sk);
        user.setCreateTime(LocalDateTime.now());
        user.setCreateUser(SessionInfo.get().getUid());
        if (userMapper.insert(user) <= 0) {
            throw new BizException(RespCode.ERROR);
        }
        //添加用户组的绑定
        req.getUserGroupIds().stream().distinct().forEach(v -> {
            UserGroupBind bind = new UserGroupBind();
            bind.setUserId(user.getId());
            bind.setUserGroupId(v);
            if (userGroupBindMapper.insert(bind) <= 0) {
                throw new BizException(RespCode.ERROR);
            }
        });
    }

    /**
     * 生成密码与盐填充到User对象中
     *
     * @param user
     * @param originPwd
     */
    public void generatorPwd(User user, String originPwd) {
        user.setSalt(IdUtil.fastSimpleUUID());
        String pwd = SecureUtil.md5(originPwd.trim() + user.getSalt());
        user.setUserPassword(pwd);
    }
}
