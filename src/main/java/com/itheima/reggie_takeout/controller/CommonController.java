package com.itheima.reggie_takeout.controller;


import com.itheima.reggie_takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basepath;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // file是一个临时文件，需要转存到指定位置，否则本次请求完成后，临时文件会被删除
        log.info("获取文件：{}", file.toString());

        // 判断一下当前目录是否存在，不存在则创建
        File fileDir = new File(basepath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        // 获取一下文件传入的原文件名
        String originalFilename = file.getOriginalFilename();

        // 我们只需要获取一下格式后缀，取子串，起始点为最后一个.
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")); // lastIndexOf(".")返回最后一个.的位置，且subString(beginIndex)截取的时候包含了起始索引

        // 为了防止出现重复的文件名，我们需要使用UUID
        String filename = UUID.randomUUID() + suffix;

        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(basepath + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 将文件名返回给前端，便于后期存入菜品的时候将图片和菜品绑定
        return R.success(filename);
    }
}
