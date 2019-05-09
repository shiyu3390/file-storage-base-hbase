package com.sinorail.common;

/**
 * @BelongsProject: photo-storage-system-boot
 * @BelongsPackage: com.sinorail.common
 * @Author: shiyu
 * @CreateTime: 2019-05-05
 */
public class Binary {

    private final byte[] data;

    public Binary(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }


    public long length() {
        return data == null ? 0 : data.length;
    }
}
