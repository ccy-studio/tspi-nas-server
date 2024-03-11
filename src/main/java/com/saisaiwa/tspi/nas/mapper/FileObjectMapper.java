package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;

import java.util.List;

/**
 * <p>
 * 文件对象 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface FileObjectMapper extends BaseMapper<FileObject> {

    /**
     * 查询全部根据存储桶id
     * @param bid
     * @return
     */
    List<FileObject> getListByBucketId(Long bid);

}
