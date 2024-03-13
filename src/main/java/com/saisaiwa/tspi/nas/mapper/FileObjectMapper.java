package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.domain.file.FObjectSearch;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
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
     *
     * @param bid
     * @return
     */
    List<FileObject> getListByBucketId(Long bid);

    /**
     * 删除全部文件根据桶id
     *
     * @param bid
     */
    void deleteByBucketsId(Long bid);


    /**
     * 获取存储桶下的root根文件夹
     *
     * @param bid
     * @return
     */
    FileObject getRootObject(Long bid);

    /**
     * 查询|搜索文件列表
     *
     * @param search
     * @return
     */
    List<FileObject> searchFileObject(FObjectSearch search);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    FileObject getByIdNoDel(Long id);

    /**
     * 删除一个文件如果是目录则一同删除目录下的所有文件
     *
     * @param path
     * @return
     */
    int deleteAllByPath(String path);

    /**
     * 查询文件根据路径前缀（包含）
     *
     * @param path
     * @return
     */
    List<FileObject> selectAllByStartPath(String path);

    /**
     * 删除-根据桶id和满足的realPath
     *
     * @param bid
     * @param paths
     * @return
     */
    int deleteByRealPathAndBucketsId(@Param("bid") Long bid, @Param("paths") Collection<String> paths);
}
