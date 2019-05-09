package com.sinorail.domain;

import com.sinorail.common.Binary;

import java.time.LocalDateTime;

/**
 * @BelongsProject: servlet-photo-storage-system
 * @BelongsPackage: com.sinorail.domain
 * @Author: shiyu
 * @CreateTime: 2019-05-05
 */
public class SmallFile {
    /**
     * 主键
     */
    private String id;

    /**
     * 文件名称
     */
    private String name;


    /**
     * 文件类型
     * image/png
     * image/jpeg
     * image/gif
     *
     */
    private String contentType;


    /**
     * 文件大小，单位：byte
     */
    private long size;

    /**
     * 上传时间
     */
    private LocalDateTime uploadDate;

    /**
     * 摘要信息
     *
     */
    private String md5;

    /**
     * 文件内容
     */
    private Binary content;

    /**
     * 文件属于哪个命名空间
     */
    private String nameSpace;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Binary getContent() {
        return content;
    }

    public void setContent(Binary content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SmallFile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", uploadDate=" + uploadDate +
                ", md5='" + md5 + '\'' +
                ", content=" + content +
                ", nameSpace=" + nameSpace +
                '}';
    }
}
