package com.saisaiwa.tspi.nas.service.impl;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.convert.UserConvert;
import com.saisaiwa.tspi.nas.domain.dto.UserGroupExtDto;
import com.saisaiwa.tspi.nas.domain.entity.ResourcesUserGroup;
import com.saisaiwa.tspi.nas.domain.entity.UserGroup;
import com.saisaiwa.tspi.nas.domain.req.UserGroupEditReq;
import com.saisaiwa.tspi.nas.domain.req.UserGroupQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupDetailVo;
import com.saisaiwa.tspi.nas.domain.vo.UserGroupListVo;
import com.saisaiwa.tspi.nas.mapper.ResourcesMapper;
import com.saisaiwa.tspi.nas.mapper.ResourcesUserGroupMapper;
import com.saisaiwa.tspi.nas.mapper.UserGroupBindMapper;
import com.saisaiwa.tspi.nas.mapper.UserGroupMapper;
import com.saisaiwa.tspi.nas.service.UserGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 15:39
 * @Version：1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserGroupServiceImpl implements UserGroupService {

    @Resource
    private UserGroupMapper userGroupMapper;

    @Resource
    private ResourcesUserGroupMapper resourcesUserGroupMapper;

    @Resource
    private UserGroupBindMapper userGroupBindMapper;

    @Resource
    private ResourcesMapper resourcesMapper;

    @Resource
    private UserConvert userConvert;

    /**
     * 新建或者更新一个用户组的信息
     *
     * @param req
     */
    @Override
    public void addOrUpdateUserGroup(UserGroupEditReq req) {
        UserGroup group = userConvert.toUserGroup(req);
        if (req.getId() == null) {
            //insert
            userGroupMapper.insert(group);
        } else {
            //update
            userGroupMapper.updateById(group);
            resourcesUserGroupMapper.deleteByUserGroupId(group.getId());
        }
        req.getResIds().stream().distinct().forEach(v -> {
            //add添加资源绑定
            ResourcesUserGroup rug = new ResourcesUserGroup();
            rug.setUserGroupId(group.getId());
            rug.setResId(v);
            resourcesUserGroupMapper.insert(rug);
        });
    }


    /**
     * 删除用户组根据用户组的ID
     *
     * @param id
     */
    @Override
    public void deleteUserGroupById(Long id) {
        if (userGroupMapper.deleteById(id) > 0) {
            resourcesUserGroupMapper.deleteByUserGroupId(id);
            userGroupBindMapper.deleteByUserGroupId(id);
        } else {
            throw new BizException(RespCode.PROMPT);
        }
    }


    /**
     * 列表查询
     *
     * @param req
     * @return
     */
    @Override
    public PageBodyResponse<UserGroupListVo> list(UserGroupQueryReq req) {
        List<UserGroupExtDto> userGroupExtDtos = userGroupMapper.selectExtInfoList(req);
        return PageBodyResponse.convert(req, userConvert.toUserGroupListVo(userGroupExtDtos));
    }

    /**
     * 查询用户组详情信息
     *
     * @param id
     * @return
     */
    @Override
    public UserGroupDetailVo getDetailById(Long id) {
        UserGroupQueryReq req = new UserGroupQueryReq();
        req.setId(id);
        List<UserGroupExtDto> list = userGroupMapper.selectExtInfoList(req);
        if (list.isEmpty()) {
            return null;
        }
        UserGroupDetailVo detailVo = userConvert.toUserGroupDetailVo(list.get(0));
        //查询Res数据信息
        detailVo.setResourceItems(userConvert.toUserGroupDetailRes(resourcesMapper.selectAllByUserGroupId(id)));
        return detailVo;
    }
}
