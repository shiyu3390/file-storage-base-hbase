package com.sinorail.service;

import com.sinorail.domain.SmallFile;

import java.util.List;
import java.util.Optional;

/**
 * @BelongsProject: servlet-photo-storage-system
 * @BelongsPackage: com.sinorail.service.impl
 * @Author: shiyu
 * @CreateTime: 2019-05-05
 */
public interface FileService {

    /**
     * 保存文件
     *
     * @param smallFile
     * @return
     */
    boolean saveFile(SmallFile smallFile);

    /**
     * 保存多个文件
     * @param smallFiles
     * @return
     */
    boolean saveFiles(List<SmallFile> smallFiles);

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    void removeFile(String namespace, String id);

    /**
     * 删除文件
     *
     * @param name
     * @return
     */
    void removeFileByName(String namespace, String name);

    /**
     * 根据id获取文件
     *
     * @param id
     * @return
     */
    Optional<SmallFile> getFileById(String namespace, String id);

    /**
     *
     * @param name
     * @return
     */
    Optional<SmallFile> getFileByName(String namespace, String name);

}
