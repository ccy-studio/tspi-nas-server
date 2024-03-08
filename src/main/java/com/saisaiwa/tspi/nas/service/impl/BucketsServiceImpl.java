package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.lang.Assert;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.convert.BucketsConvert;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.PoliciesRule;
import com.saisaiwa.tspi.nas.domain.enums.BucketsActionEnum;
import com.saisaiwa.tspi.nas.domain.req.BucketsEditReq;
import com.saisaiwa.tspi.nas.mapper.BucketsMapper;
import com.saisaiwa.tspi.nas.mapper.PoliciesRuleMapper;
import com.saisaiwa.tspi.nas.service.BucketsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            PoliciesRule policiesRule = new PoliciesRule();
            policiesRule.setBucketsId(buckets.getId());
            policiesRule.setCreateUser(buckets.getCreateUser());
            policiesRule.setEffect(true);
            policiesRule.setAction(BucketsActionEnum.SUPER_AUTH.getPremiss());
            policiesRuleMapper.insert(policiesRule);
            //todo 路径文件新建
        } else {
            //部分字段修改
            Buckets dbBuckets = bucketsMapper.selectById(req.getId());
            Assert.notNull(dbBuckets);
            dbBuckets.setResId(req.getResId());
            dbBuckets.setPermissions(req.getPermissions());
            dbBuckets.setPermissionsScope(req.getPermissionsScope());
            dbBuckets.setStaticPage(req.getStaticPage());
            dbBuckets.setUpdateUser(SessionInfo.get().getUid());
            bucketsMapper.updateById(dbBuckets);
        }
    }
}
