package com.saisaiwa.tspi.nas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saisaiwa.tspi.nas.domain.dto.FileObjectShareExtDto;
import com.saisaiwa.tspi.nas.domain.entity.FileObjectShare;
import com.saisaiwa.tspi.nas.domain.req.FileShareListQueryReq;

import java.util.List;

/**
 * <p>
 * 文件对象分享 Mapper 接口
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
public interface FileObjectShareMapper extends BaseMapper<FileObjectShare> {

    void deleteByCreateUser(Long uid);

    /**
     * 查询此用户是否存在此指定的文件ID的分享
     *
     * @param fid
     * @param uid
     * @return
     */
    FileObjectShare getUserShareByObjectId(Long fid, Long uid);

    FileObjectShare getBySignKey(String key);

    FileObjectShareExtDto getExtBySignKey(String key);

    List<FileObjectShareExtDto> selectShareFileObjectExt(FileShareListQueryReq req);

}
