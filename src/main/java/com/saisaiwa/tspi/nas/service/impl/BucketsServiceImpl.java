package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.common.file.FileLocalScanService;
import com.saisaiwa.tspi.nas.common.file.FileNativeService;
import com.saisaiwa.tspi.nas.domain.convert.BucketsConvert;
import com.saisaiwa.tspi.nas.domain.dto.BucketsExtDto;
import com.saisaiwa.tspi.nas.domain.dto.BucketsPremissDto;
import com.saisaiwa.tspi.nas.domain.dto.PoliciesRuleExtDto;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.PoliciesRule;
import com.saisaiwa.tspi.nas.domain.entity.Resources;
import com.saisaiwa.tspi.nas.domain.enums.BucketsACLEnum;
import com.saisaiwa.tspi.nas.domain.enums.BucketsPermissionEnum;
import com.saisaiwa.tspi.nas.domain.enums.BucketsPermissionScopeEnum;
import com.saisaiwa.tspi.nas.domain.enums.UserEnum;
import com.saisaiwa.tspi.nas.domain.req.BucketsAclQueryReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsAclReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.domain.req.BucketsQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.BucketsAclInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsDetailVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.BucketsPermissionUserVo;
import com.saisaiwa.tspi.nas.mapper.BucketsMapper;
import com.saisaiwa.tspi.nas.mapper.PoliciesRuleMapper;
import com.saisaiwa.tspi.nas.mapper.UserMapper;
import com.saisaiwa.tspi.nas.service.BucketsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:14
 * @Version：1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BucketsServiceImpl implements BucketsService {

    @Resource
    private BucketsMapper bucketsMapper;

    @Resource
    private PoliciesRuleMapper policiesRuleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FileNativeService fileNativeService;

    @Resource
    private FileLocalScanService localScanTask;

    /**
     * 创建或者修改存储桶
     *
     * @param req
     */
    @Override
    public void createBuckets(BucketsEditReq req) {
        Buckets buckets = BucketsConvert.INSTANCE.toBuckets(req);
        if (buckets.getId() == null) {
            //insert
            //检查名称是否重复
            if (bucketsMapper.selectByBucketsName(req.getBucketsName()) != null) {
                throw new BizException("此名称已经存在");
            }
            if (bucketsMapper.isOtherContain(req.getMountPoint())) {
                throw new BizException("该挂载点不可作为已存在挂载点的子目录");
            }
            buckets.setCreateUser(SessionInfo.get().getUid());
            bucketsMapper.insert(buckets);
            //添加存储桶的权限策略
            PoliciesRule policiesRule = new PoliciesRule();
            policiesRule.setBucketsId(buckets.getId());
            policiesRule.setCreateUser(buckets.getCreateUser());
            policiesRule.setEffect(true);
            policiesRule.setAction(BucketsACLEnum.SUPER_AUTH.getPremiss());
            policiesRule.setUserId(SessionInfo.get().getUid());
            policiesRule.setCreateUser(SessionInfo.get().getUid());
            policiesRuleMapper.insert(policiesRule);
            //新建存储桶
            fileNativeService.createBuckets(buckets);
            localScanTask.addListener(buckets);
        } else {
            //部分字段修改
            Buckets dbBuckets = bucketsMapper.selectById(req.getId());
            Assert.notNull(dbBuckets);
            dbBuckets.setPermissions(req.getPermissions());
            dbBuckets.setPermissionsScope(req.getPermissionsScope());
            dbBuckets.setStaticPage(req.getStaticPage());
            dbBuckets.setUpdateUser(SessionInfo.get().getUid());
            bucketsMapper.updateById(dbBuckets);
        }
    }

    /**
     * 根据ID获取详细的信息
     *
     * @param bid
     * @return
     */
    @Override
    public BucketsDetailVo getDetailBucketById(Long bid) {
        Buckets buckets = bucketsMapper.selectById(bid);
        Assert.notNull(buckets);
        return BucketsConvert.INSTANCE.toBucketsDetailVo(buckets);
    }

    /**
     * 删除一个存储桶根据ID
     *
     * @param bid
     */
    @Override
    public void deleteBucketById(Long bid) {
        Buckets buckets = bucketsMapper.selectById(bid);
        Assert.notNull(buckets);
        if (bucketsMapper.deleteById(bid) <= 0) {
            throw new BizException(RespCode.ERROR);
        }
        //移除监听器
        localScanTask.removeListener(buckets);
        //删除
        fileNativeService.deleteBuckets(buckets);
    }


    /**
     * 添加一个ACL权限
     *
     * @param req
     */
    @Override
    public void addOrUpdateAcl(BucketsAclReq req) {
        Buckets buckets = bucketsMapper.selectById(req.getBucketsId());
        Assert.notNull(buckets);
        PoliciesRule rule = policiesRuleMapper.selectByUserIdAndBucketsId(req.getUserId(), req.getBucketsId());
        if (rule == null) {
            rule = new PoliciesRule();
            rule.setUserId(req.getUserId());
            rule.setBucketsId(req.getBucketsId());
            rule.setEffect(req.getEffect());
            rule.setAction(StrUtil.join(";", req.getAcl()));
            rule.setCreateUser(SessionInfo.get().getUid());
            policiesRuleMapper.insert(rule);
        } else {
            if (BucketsACLEnum.SUPER_AUTH.check(rule.getAction())) {
                throw new BizException("错误:管理员不可修改");
            }
            rule.setEffect(req.getEffect());
            rule.setAction(StrUtil.join(";", req.getAcl()));
            rule.setUpdateUser(SessionInfo.get().getUid());
            policiesRuleMapper.updateById(rule);
        }
    }

    /**
     * 根据ACL的ID删除此ACL
     *
     * @param id
     */
    @Override
    public void deleteAclById(Long id) {
        PoliciesRule rule = policiesRuleMapper.selectById(id);
        Assert.notNull(rule);
        if (BucketsACLEnum.SUPER_AUTH.check(rule.getAction())) {
            throw new BizException("错误：无法删除默认ACL");
        }
        policiesRuleMapper.deleteById(id);
    }


    /**
     * 获取桶的ACL列表数据
     *
     * @param req
     * @return
     */
    @Override
    public PageBodyResponse<BucketsAclInfoVo> getAclByBucketsAll(BucketsAclQueryReq req) {
        List<PoliciesRuleExtDto> dtos = policiesRuleMapper.selectExtAll(req);
        List<BucketsAclInfoVo> infoVo = BucketsConvert.INSTANCE.toBucketsAclInfoVo(dtos);
        infoVo.forEach(v -> {
            v.setAcl(StrUtil.split(v.getAction(), ";"));
            List<Resources> resources = userMapper.selectResAllByUserBinds(v.getUserId());
            boolean contain = resources.stream().map(Resources::getId).toList().contains(v.getBucketsResId());
            v.setIsResContain(contain);
        });
        return PageBodyResponse.convert(req, infoVo);
    }


    /**
     * 查询此用户可见的存储桶
     *
     * @param req
     * @return
     */
    @Override
    public List<BucketsInfoVo> getBucketAll(BucketsQueryReq req) {
        Long uid = SessionInfo.get().getUid();
        if (uid.intValue() == UserEnum.ADMIN.getCode()) {
            uid = null;
        }
        List<BucketsExtDto> dtoList = bucketsMapper.selectTableList(req.getId(), req.getKeyword(), uid);
        List<BucketsInfoVo> infoVos = BucketsConvert.INSTANCE.toBucketsInfoVo(dtoList);
        if (Boolean.TRUE.equals(req.getDisplayPermission())) {
            for (BucketsInfoVo infoVo : infoVos) {
                BucketsPermissionUserVo vo;
                if (uid == null) {
                    vo = new BucketsPermissionUserVo();
                    vo.setRead(true);
                    vo.setWrite(true);
                    vo.setDelete(true);
                    vo.setShare(true);
                    vo.setManage(true);
                } else {
                    vo = getPermissionByUser(infoVo.getId(), uid);
                }
                infoVo.setAcl(vo);
            }
        }
        return infoVos;
    }

    /**
     * 获取此用户对存储桶所具有的权限信息
     *
     * @param bucketsId
     * @param uid
     * @return
     */
    @Override
    public BucketsPermissionUserVo getPermissionByUser(Long bucketsId, Long uid) {
        BucketsPremissDto dto = bucketsMapper.selectBucketsPremissByUid(bucketsId, uid);
        if (dto == null) {
            return null;
        }
        BucketsPermissionUserVo vo = new BucketsPermissionUserVo();
        vo.setBucketsId(bucketsId);
        List<String> actions = StrUtil.split(dto.getAction(), ";");
        if (StrUtil.isNotBlank(dto.getAction()) && dto.getAction().contains(BucketsACLEnum.SUPER_AUTH.getPremiss())) {
            //判断是管理员用户
            vo.setRead(true);
            vo.setWrite(true);
            vo.setDelete(true);
            vo.setShare(true);
            vo.setManage(true);
        } else if (dto.getPermissions().equals(BucketsPermissionEnum.PR_PRIVATE.getPremiss())
                && !dto.getPermissionsScope().equals(BucketsPermissionScopeEnum.PS_PRIVATE.getPremiss())) {
            //桶是私有权限且是非私有的范围
            if (dto.getPermissionsScope().equals(BucketsPermissionScopeEnum.PS_GROUP.getPremiss()) && dto.getUserId() == null) {
                //用户不在组内拒绝
                return vo;
            }
            if (!actions.isEmpty() && dto.getEffect() != null) {
                if (dto.getEffect()) {
                    //允许策略
                    if (actions.contains(BucketsACLEnum.GET_OBJ.getPremiss())) {
                        vo.setRead(true);
                    } else if (actions.contains(BucketsACLEnum.DEL_OBJ.getPremiss())) {
                        vo.setDelete(true);
                    } else if (actions.contains(BucketsACLEnum.PUT_OBJ.getPremiss())) {
                        vo.setWrite(true);
                    } else if (actions.contains(BucketsACLEnum.SHARE_OBJ.getPremiss())) {
                        vo.setShare(true);
                    }
                } else {
                    //拒绝策略
                    vo.setRead(true);
                    vo.setWrite(true);
                    vo.setDelete(true);
                    vo.setShare(true);
                    if (actions.contains(BucketsACLEnum.GET_OBJ.getPremiss())) {
                        vo.setRead(false);
                        vo.setWrite(false);
                        vo.setDelete(false);
                        vo.setShare(false);
                    } else if (actions.contains(BucketsACLEnum.DEL_OBJ.getPremiss())) {
                        vo.setDelete(false);
                    } else if (actions.contains(BucketsACLEnum.PUT_OBJ.getPremiss())) {
                        vo.setWrite(false);
                    } else if (actions.contains(BucketsACLEnum.SHARE_OBJ.getPremiss())) {
                        vo.setShare(false);
                    }
                }
            }
        } else if (!dto.getPermissionsScope().equals(BucketsPermissionScopeEnum.PS_PRIVATE.getPremiss())) {
            //可能是1公读公写、2公读私写。但是权限范围不是私有的
            if (dto.getPermissionsScope().equals(BucketsPermissionScopeEnum.PS_GROUP.getPremiss()) && dto.getUserId() == null) {
                //用户不在组内拒绝
                return vo;
            }
            if (dto.getPermissions().equals(BucketsPermissionEnum.PR_R.getPremiss())) {
                vo.setRead(true);
            } else if (dto.getPermissions().equals(BucketsPermissionEnum.PR_RW.getPremiss())) {
                vo.setRead(true);
                vo.setWrite(true);
            }
            //再次判断策略配置
            if (!actions.isEmpty() && dto.getEffect() != null) {
                if (dto.getEffect()) {
                    //允许策略
                    if (actions.contains(BucketsACLEnum.SHARE_OBJ.getPremiss())) {
                        vo.setShare(true);
                    }
                    if (actions.contains(BucketsACLEnum.DEL_OBJ.getPremiss())) {
                        vo.setDelete(true);
                    }
                } else {
                    //拒绝策略
                    if (!actions.contains(BucketsACLEnum.SHARE_OBJ.getPremiss())) {
                        vo.setShare(true);
                    }
                    if (actions.contains(BucketsACLEnum.GET_OBJ.getPremiss())) {
                        vo.setRead(false);
                        vo.setWrite(false);
                        vo.setDelete(false);
                        vo.setShare(false);
                    } else if (actions.contains(BucketsACLEnum.DEL_OBJ.getPremiss())) {
                        vo.setDelete(false);
                    } else if (actions.contains(BucketsACLEnum.PUT_OBJ.getPremiss())) {
                        vo.setWrite(false);
                    }
                }
            }
        }
        return vo;
    }

}
