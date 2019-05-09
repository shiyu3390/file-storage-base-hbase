package com.sinorail.control;

import com.sinorail.common.Binary;
import com.sinorail.domain.SmallFile;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sinorail.service.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @BelongsProject: photo-storage-system-boot
 * @BelongsPackage: com.sinorail.control
 * @Author: shiyu
 * @CreateTime: 2019-05-05
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3306)
@RequestMapping(value = "/api")
public class FileApiController {
    @Autowired
    private FileService fileService;

    private Logger logger = LoggerFactory.getLogger(FileApiController.class);

    /**
     * @param namespace
     * @param id
     * @return
     */
    @ApiOperation(value = "删除文件", notes = "根据文件在Hbase中的唯一ID删除")
    @RequestMapping(value = "delete", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE}, produces = {"application/json"})
    public Map<String, Object> delete(@RequestParam(value = "namespace") String namespace,
                                      @RequestParam(value = "id") String id) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            fileService.removeFile(namespace, id);
            result.put("code", HttpStatus.SC_OK);
            result.put("message", "Delete success");
        } catch (Throwable e) {
            logger.error("Delete occur {} .", e.getMessage(), e);
            result.put("code", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            result.put("message", "Delete failed");
        }
        return result;
    }

    /**
     * @param namespace
     * @param name
     * @return
     */
    @ApiOperation(value = "删除文件", notes = "根据文件名删除")
    @RequestMapping(value = "deleteByName", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE}, produces = {"application/json"})
    public Map<String, Object> deleteByName(@RequestParam(value = "namespace") String namespace,
                                            @RequestParam(value = "name") String name) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            fileService.removeFile(namespace, name);
            result.put("code", HttpStatus.SC_OK);
            result.put("message", "Delete success");
        } catch (Throwable e) {
            logger.error("Delete occur {} .", e.getMessage(), e);
            result.put("code", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            result.put("message", "Delete failed");
        }
        return result;
    }

    /**
     * @param req
     * @return
     */
    @ApiOperation(value = "上传文件", notes = "支持多个文件批量上传")
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = {"application/json"})
    public Map<String, Object> upload(HttpServletRequest req) {
        HashMap<String, Object> result = new HashMap<>();
        int code = HttpStatus.SC_OK;
        String message = "Upload file success";
        try {
            Collection<Part> parts = req.getParts();
            String nameSpace = req.getHeader("namespace");
            List<SmallFile> smallFiles = new ArrayList<>();
            for (Part part : parts) {
                SmallFile smallFile = new SmallFile();
                smallFile.setName(part.getSubmittedFileName());
                smallFile.setContentType(part.getContentType());
                smallFile.setNameSpace(nameSpace);
                try (InputStream is = part.getInputStream()) {
                    smallFile.setContent(new Binary(IOUtils.toByteArray(is)));
                }
                smallFiles.add(smallFile);
            }
            if (smallFiles.size() == 0) {
                code = HttpStatus.SC_BAD_REQUEST;
                message = "Not Receive a file";
            } else {
                boolean success = fileService.saveFiles(smallFiles);
                if (!success) {
                    code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
                    message = "Upload file fail";
                }
            }
        } catch (Throwable e) {
            code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            message = "Upload file fail";
            logger.error("Upload occur {} .", e.getMessage(), e);
        }
        result.put("code", code);
        result.put("message", message);
        return result;
    }

    /**
     * @param namespace
     * @param id
     * @param response
     */
    @ApiOperation(value = "下载文件", notes = "根据文件在Hbase中的唯一ID下载")
    @RequestMapping(value = "/show/{namespace}/{id}", method = RequestMethod.GET)
    public void show(@PathVariable(value = "namespace") String namespace, @PathVariable(value = "id") String id, HttpServletResponse response) {
        Optional<SmallFile> optional = fileService.getFileById(namespace, id);
        if (optional.isPresent()) {
            SmallFile smallFile = optional.get();
            byte[] content = smallFile.getContent().getData();
            try {
                response.setContentType(smallFile.getContentType());
                response.getOutputStream().write(content);
                response.flushBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param namespace
     * @param name
     * @param response
     */
    @ApiOperation(value = "下载文件", notes = "根据文件名下载")
    @RequestMapping(value = "showByName")
    public void showByName(@RequestParam(value = "namespace") String namespace,
                           @RequestParam(value = "name") String name,
                           HttpServletResponse response) {
        Optional<SmallFile> optional = fileService.getFileByName(namespace, name);
        if (optional.isPresent()) {
            SmallFile smallFile = optional.get();
            byte[] content = smallFile.getContent().getData();
            try {
                response.setContentType(smallFile.getContentType());
                response.getOutputStream().write(content);
                response.flushBuffer();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
}
