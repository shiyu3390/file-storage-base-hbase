package com.sinorail.control;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest

public class FileApiControllerTest {

    private MockMvc mockMvc; // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。

    @Autowired
    private WebApplicationContext wac; // 注入WebApplicationContext

    @Before // 在测试开始前初始化工作
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void delete() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.DELETE, new URI("/api/delete"))
                .param("namespace", "test2")
                .param("id", "502a3e9f4f5d6cca4b0ae21716a297a0");
        String result = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void upload() throws Exception {
        String imgPath2 = "D:\\google download\\Java-3.jpg";
        File file2 = new File(imgPath2);
        FileInputStream fis = new FileInputStream(file2);
        byte[] bbb = new byte[fis.available()];//读图为流
        fis.read(bbb);//将文件内容写入字节数组
        fis.close();

        MockPart mockPart = new MockPart("file", "Java-3.jpg", bbb);
        HttpHeaders headers = mockPart.getHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(new URI("/api/upload"))
                .part(mockPart)
                .header("namespace", "test2");

        String result = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void showByName() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.GET, new URI("/api/showByName"))
                .param("namespace", "test2")
                .param("name", "Java-3.jpg");
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        String contentType = response.getContentType();
        System.out.println(contentType);
        Assert.assertEquals("image/jpeg", contentType);
//        FileUtils.writeByteArrayToFile(new File("K:\\Java-3.jpg"),response.getContentAsByteArray());
    }

    @Test
    public void show() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(HttpMethod.GET,
                        new URI("/api/show/" + "test2/" +
                                "502a3e9f4f5d6cca4b0ae21716a297a0"));
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        String contentType = response.getContentType();
        System.out.println(contentType);
        Assert.assertEquals("image/jpeg", contentType);
        FileUtils.writeByteArrayToFile(new File("K:\\Java-4.jpg"), response.getContentAsByteArray());
    }

}