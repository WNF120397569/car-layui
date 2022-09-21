package com.yimin.carlayui.controller;

import com.yimin.carlayui.common.Result;
import com.yimin.carlayui.util.FileUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@Slf4j
public class FileController {

    @PostMapping("/upload")
    @ResponseBody
    public Result upload(@RequestParam("file") MultipartFile file, @RequestParam("key") String key, HttpSession session) {
        log.debug("key=" + key);
        List<String> imgs = (List<String>) session.getAttribute(key);
        if (imgs == null) {
            log.debug("创建了imgs");
            imgs = new ArrayList<>();
        }

        //上传文件
        try {
            String filePath = FileUtil.upload(file);
            log.debug("filePath=" + filePath);
            imgs.add(filePath);
            session.setAttribute(key, imgs);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败，服务器发生异常");
        }

        List<String> imgs2 = (List<String>) session.getAttribute(key);
        log.debug("上传后session中的imgs=" + imgs2);

        return Result.success("上传成功");

    }
}