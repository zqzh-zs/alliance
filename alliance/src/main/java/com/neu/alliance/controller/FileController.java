package com.neu.alliance.controller;

import cn.hutool.core.io.FileUtil;
import com.neu.alliance.common.config.pojo.Result;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Title: FileController
 * @Author 曦
 * @Date 2025/6/19 18:14
 * @description:
 */

@RestController
@RequestMapping("/files")
public class FileController {
    //本地磁盘文件的存储路径 D:\JavaWebProject\alliance\files\
    private static final String filePath = System.getProperty("user.dir") + "/files/";
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "oldAvatar", required = false) String oldAvatar,
                         HttpServletRequest request) throws IOException {
        if (!FileUtil.exist(filePath)) {
            FileUtil.mkdir(filePath);
        }

        // 删除旧头像（如果有）
        if (StringUtils.isNotBlank(oldAvatar) && oldAvatar.contains("/files/download/")) {
            String oldFileName = oldAvatar.substring(oldAvatar.lastIndexOf("/") + 1);
            File oldFile = new File(filePath + oldFileName);
            if (oldFile.exists()) oldFile.delete();
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String ext = FileUtil.extName(originalFilename).toLowerCase();
        if (!List.of("jpg", "jpeg", "png", "gif").contains(ext)) {
            return Result.fail("不支持的文件类型");
        }

        // 拼接新文件名
        String filename = FileUtil.mainName(originalFilename) + "_" + System.currentTimeMillis() + "." + ext;
        String realPath = filePath + filename;
        file.transferTo(new File(realPath));

        // 构建可访问路径
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + "/files/download/" + filename;
        return Result.ok(url);
    }


    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        String realPath = filePath + fileName;
        if (!FileUtil.exist(realPath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.addHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        byte[] bytes = FileUtil.readBytes(realPath);
        ServletOutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }

}

