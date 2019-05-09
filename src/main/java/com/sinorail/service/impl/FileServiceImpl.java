package com.sinorail.service.impl;

import com.sinorail.dao.SmallFileDao;
import com.sinorail.domain.SmallFile;
import com.sinorail.service.FileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @BelongsProject: servlet-photo-storage-system
 * @BelongsPackage: com.sinorail.service.impl
 * @Author: shiyu
 * @CreateTime: 2019-05-05
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private SmallFileDao smallFileDao;

    @Transactional
    @Override
    public boolean saveFile(SmallFile smallFile) {
        //Id生成策略
        //UUID
        //时间戳+自增长的编号
        smallFile.setId(UUID.randomUUID().toString());
        smallFile.setUploadDate(LocalDateTime.now());
        smallFile.setMd5(DigestUtils.md5Hex(smallFile.getContent().getData()));
        return smallFileDao.save(smallFile);
    }

    @Transactional
    @Override
    public boolean saveFiles(List<SmallFile> smallFiles) {

        for (SmallFile smallFile : smallFiles) {
            smallFile.setId(UUID.randomUUID().toString());
            smallFile.setUploadDate(LocalDateTime.now());
            smallFile.setMd5(DigestUtils.md5Hex(smallFile.getContent().getData()));
        }
        return smallFileDao.saveFiles(smallFiles);
    }

    @Transactional
    @Override
    public void removeFile(String namespace, String id) {
        smallFileDao.delete(namespace, id);
    }

    @Transactional
    @Override
    public void removeFileByName(String namespace, String name) {
        String rowKey = DigestUtils.md5Hex(name);
        removeFile(namespace,rowKey);
    }

    @Override
    public Optional<SmallFile> getFileById(String namespace, String id) {
        return Optional.ofNullable(smallFileDao.find(namespace, id));
    }

    @Override
    public Optional<SmallFile> getFileByName(String namespace, String name) {
        String rowKey = DigestUtils.md5Hex(name);
        return getFileById(namespace, rowKey);
    }
}