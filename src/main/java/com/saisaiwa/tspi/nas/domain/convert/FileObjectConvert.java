package com.saisaiwa.tspi.nas.domain.convert;

import com.saisaiwa.tspi.nas.domain.dto.FileObjectShareExtDto;
import com.saisaiwa.tspi.nas.domain.entity.FileBlockRecords;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.domain.vo.FileBlockInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.FileObjectInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.FileShareInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:22
 * @Version：1.0
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FileObjectConvert {
    FileObjectConvert INSTANCE = Mappers.getMapper(FileObjectConvert.class);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    FileObjectInfoVo toFileObjectInfoVo(FileObject object);

    List<FileObjectInfoVo> toFileObjectInfoVo(List<FileObject> object);


    FileBlockInfoVo toFileBlockInfoVo(FileBlockRecords records);

    @Mapping(target = "expirationTime", dateFormat = "yyyy-MM-dd HH:mm")
    FileShareInfoVo toFileShareInfoVo(FileObjectShareExtDto share);
    List<FileShareInfoVo> toFileShareInfoVo(List<FileObjectShareExtDto> share);
}
