package com.sinorail;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import java.io.File;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
        File[] fileArr = new File[3];
        String imgPath1 = "K:\\20190417133360.jpg";
        File file1 = new File(imgPath1);
        fileArr[0] = file1;
        String imgPath2 = "D:\\google download\\Java-3.jpg";
        File file2 = new File(imgPath2);
        fileArr[1] = file2;
        String imgPath3 = "D:\\google download\\Java-16.png";
        File file3 = new File(imgPath3);
        fileArr[2] = file3;
        HttpResponse response = HttpUtil.createPost("localhost:8008/api/upload")
                .header("namespace","test")
                .form("filename", fileArr)
                .execute();
        int status = response.getStatus();
        System.out.println(status);
        System.out.println(response.body());
    }

    public static String reverse(String src) {
        StringBuffer sb = new StringBuffer();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < src.length(); i++) {
            char at = src.charAt(i);
            if ((at >= 'a' && at <= 'z') || (at >= 'A' && at <= 'Z')) {
                stack.push(at);
            }else {
                while (!stack.empty()) {
                    sb.append(stack.pop());
                }
                sb.append(at);
            }
        }
        while (!stack.empty()) {
            sb.append(stack.pop());
        }
        return sb.toString();
    }
}
