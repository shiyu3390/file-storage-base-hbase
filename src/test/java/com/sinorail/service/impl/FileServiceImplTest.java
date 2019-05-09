package com.sinorail.service.impl;

import com.sinorail.common.Binary;
import com.sinorail.domain.SmallFile;
import com.sinorail.service.FileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest

public class FileServiceImplTest {
    @Autowired
    private FileService fileService;

    @Test
    public void saveFile() throws Exception {
        SmallFile smallFile = new SmallFile();
        String imgPath = "K:\\20190417133359.jpg";
        File file = new File(imgPath);
        smallFile.setName(file.getName());
        smallFile.setContentType("jpg");
        FileInputStream fis = new FileInputStream(file);
        byte[] bbb = new byte[fis.available()];//读图为流
        fis.read(bbb);//将文件内容写入字节数组
        fis.close();
        smallFile.setSize(bbb.length);
        smallFile.setContent(new Binary(bbb));
        boolean saveFile = fileService.saveFile(smallFile);
        Assert.assertTrue(saveFile);
    }

    @Test
    public void removeFile() throws Exception {
    }

    @Test
    public void getFileById() throws Exception {
        String name = "20190417133359.jpg";
        String rowKey = DigestUtils.md5Hex(name);
        String namespace = "test";
        Optional<SmallFile> smallFile = fileService.getFileById(namespace, rowKey);
        Assert.assertNotNull(smallFile);
    }

    @Test
    public void listFilesByPage() throws Exception {
    }

}