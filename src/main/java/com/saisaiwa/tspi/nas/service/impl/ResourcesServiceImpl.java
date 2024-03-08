package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.lang.Assert;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.convert.ResourcesConvert;
import com.saisaiwa.tspi.nas.domain.entity.Resources;
import com.saisaiwa.tspi.nas.domain.req.ResourcesEditReq;
import com.saisaiwa.tspi.nas.domain.req.ResourcesQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.ResourcesInfoVo;
import com.saisaiwa.tspi.nas.mapper.ResourcesMapper;
import com.saisaiwa.tspi.nas.mapper.ResourcesUserGroupMapper;
import com.saisaiwa.tspi.nas.service.ResourcesService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 17:42
 * @Version：1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourcesServiceImpl implements ResourcesService {

    @Resource
    private ResourcesMapper resourcesMapper;

    @Resource
    private ResourcesUserGroupMapper resourcesUserGroupMapper;

    /**
     * 新增或者修改资源
     *
     * @param req
     */
    @Override
    public void saveOrUpdateResources(ResourcesEditReq req) {
        Resources resources = ResourcesConvert.INSTANCE.toResources(req);
        if (req.getId() == null) {
            //insert
            resources.setCreateUser(SessionInfo.get().getUid());
            resourcesMapper.insert(resources);
        } else {
            //update
            resources.setUpdateUser(SessionInfo.get().getUid());
            resourcesMapper.updateById(resources);
        }
    }

    /**
     * 删除资源根据ID
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        Resources resources = resourcesMapper.selectById(id);
        Assert.notNull(resources);
        if (resourcesUserGroupMapper.countByResId(id) != 0) {
            throw new BizException("请先解绑用户组的绑定在删除");
        }
        if (resourcesMapper.deleteById(id) <= 0) {
            throw new BizException(RespCode.PROMPT);
        }
    }

    /**
     * 查询资源列表数据
     *
     * @param req
     * @return
     */
    @Override
    public PageBodyResponse<ResourcesInfoVo> selectList(ResourcesQueryReq req) {
        List<Resources> resources = resourcesMapper.selectAllByCondition(req);
        return PageBodyResponse.convert(req, ResourcesConvert.INSTANCE.toResourcesInfoVo(resources));
    }

    /**
     * 查询资源详情根据ID
     *
     * @param id
     * @return
     */
    @Override
    public ResourcesInfoVo getDetailById(Long id) {
        ResourcesQueryReq req = new ResourcesQueryReq();
        req.setId(id);
        List<Resources> resources = resourcesMapper.selectAllByCondition(req);
        if (resources.isEmpty()) {
            return null;
        }
        return ResourcesConvert.INSTANCE.toResourcesInfoVo(resources.get(0));
    }
}
