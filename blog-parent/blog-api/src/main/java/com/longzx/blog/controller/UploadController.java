package com.longzx.blog.controller;

import com.longzx.blog.utils.QiniuUtils;
import com.longzx.blog.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    QiniuUtils qiniuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file){
        //获取文件的原始名称：xxx.png/jpg
        String originalFilename = file.getOriginalFilename();
        String uuidName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");

        boolean upload = qiniuUtils.upload(file, uuidName);
        if (upload) return Result.success(QiniuUtils.url + uuidName);
        return Result.fail(20001, "文件上传失败");
    }
}
