package com.saisaiwa.tspi.nas.service;

import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.domain.entity.FileBlockRecords;
import com.saisaiwa.tspi.nas.domain.file.*;
import com.saisaiwa.tspi.nas.domain.req.FileObjectShareGetReq;
import com.saisaiwa.tspi.nas.domain.vo.FileBlockInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.FileObjectInfoVo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @date: 2024/03/13 15:55
 * @author: saisiawa
 **/
public interface FileObjectService {
    PageBodyResponse<FileObjectInfoVo> selectObjectAll(FObjectSearch search);

    void deleteObject(FObjectDelete dat);

    void copyFile(FObjectCopy dat);

    void moveFile(FObjectCopy dat);

    void rename(FObjectRename dat);

    void createFolder(FObjectUpload dat);

    boolean hasFile(FObjectHas has);

    void uploadFileSign(MultipartFile file, FObjectUpload dat);

    FileBlockRecords initUploadBlock(FObjectUploadBlock dat);

    FileBlockInfoVo getBlockInfo(Long blockId);

    FileBlockInfoVo uploadFileBlock(MultipartFile file, FObjectUploadBlock dat);

    FileObjectInfoVo fileBlockMerge(Long blockId);

    ResponseEntity<InputStreamResource> getFileObjectStream(FObjectGet dat, String range);

    ResponseEntity<?> getShareFileObject(FileObjectShareGetReq req);
}
