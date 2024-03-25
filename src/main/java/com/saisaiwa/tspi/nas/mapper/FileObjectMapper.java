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
     * 检查指定的存储桶内是否具有重复的值存在
     *
     * @param bucketId
     * @param realPaths
     * @return
     */
    List<FileObject> checkRealPathTheDuplicateValue(@Param("bucketId") Long bucketId, @Param("realPaths") List<String> realPaths);

    /**
     * 查询一个根据文件名称和父ID
     *
     * @param fileName
     * @param pid
     * @return
     */
    FileObject getByFileNameAndParentId(@Param("fileName") String fileName, @Param("pid") Long pid);

    /**
     * 查询全部根据存储桶id
     *
     * @param bid
     * @return
     */
    List<FileObject> getListByBucketId(Long bid);

    /**
     * 查询全部文件只筛选文件夹的数据
     *
     * @param bid
     * @return
     */
    List<FileObject> getDirAllListByBucketId(Long bid);

    /**
     * 删除全部文件根据桶id
     *
     * @param bid
     */
    int deleteByBucketsId(Long bid);

    /**
     * 删除指定的RealPath开头的文件数据，但不包含自身
     *
     * @param realPath
     */
    int deletePartByRealWithStartNotSelf(String realPath);


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
    int deleteAllByFilePathAndStartWith(String path);

    /**
     * 删除一个文件如果是目录则一同删除目录下的所有文件
     *
     * @param path
     * @return
     */
    int deleteAllByRealPathAndStartWith(String path);

    /**
     * 删除一个文件如果是目录则一同删除目录下的所有文件 批量操作
     *
     * @param path
     * @return
     */
    int deleteAllByRealPathAndStartWithBatch(List<String> path);

    /**
     * 查询文件根据路径前缀（包含）
     *
     * @param path 绝对路径
     * @return
     */
    List<FileObject> selectAllByStartFilePath(String path);

    /**
     * 查询文件根据路径前缀（包含）
     *
     * @param path 物理路径
     * @return
     */
    List<FileObject> selectAllByStartRealPath(String path);

    /**
     * 删除-根据桶id和满足的realPath
     *
     * @param bid
     * @param paths
     * @return
     */
    int deleteByRealPathAndBucketsId(@Param("bid") Long bid, @Param("paths") Collection<String> paths);


    /**
     * 查询根据物理路径相等且执定的桶ID查询一个
     *
     * @param realPath
     * @param bid      可选
     * @return
     */
    FileObject getByRealPathAndBucketsId(@Param("realPath") String realPath, @Param("bid") Long bid);


}
