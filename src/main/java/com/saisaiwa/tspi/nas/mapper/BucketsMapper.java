package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.dto.BucketsExtDto;
import com.saisaiwa.tspi.nas.domain.dto.BucketsPremissDto;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 存储桶 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface BucketsMapper extends BaseMapper<Buckets> {

    /**
     * 查询桶根据名称
     *
     * @param name
     * @return
     */
    BucketsMapper selectByBucketsName(String name);

    /**
     * 判断此路径是否包含存在
     *
     * @param path
     * @return
     */
    boolean isOtherContain(String path);

    /**
     * 查询存储桶列表
     *
     * @param id
     * @param keyword
     * @param uid     为空则不限制全部查询，传入用户ID则查询此用户可看的数据
     * @return
     */
    List<BucketsExtDto> selectList(@Param("id") Long id, @Param("keyword") String keyword, @Param("uid") Long uid);

    /**
     * 查询存储桶的权限信息，根据用户ID为依据
     *
     * @param bkId
     * @param uid
     * @return
     */
    BucketsPremissDto selectBucketsPremissByUid(@Param("bkId") Long bkId, @Param("uid") Long uid);
}
