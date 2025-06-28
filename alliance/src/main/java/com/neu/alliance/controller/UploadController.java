package com.neu.alliance.controller;


import com.neu.alliance.entity.Response;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class UploadController {

    // 上传文件保存的路径（项目根目录下的 uploads 文件夹）
    private final String UPLOAD_DIR = "./uploads/";

    @PostMapping("/meeting/upload")
    public Response<ResponseData> uploadImage(@RequestHeader(value = "Authorization", required = false) String authHeader,@RequestParam("cover") MultipartFile file) {
        System.out.println("Authorization: " + authHeader);
        if (file.isEmpty()) {
            return Response.error("未上传文件");
        }

        try {
            // 创建上传目录（如果不存在）
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String extension = StringUtils.getFilenameExtension(originalFilename);

            // 生成新文件名：时间戳 + 后缀
            String newFileName = System.currentTimeMillis() + "." + extension;

            // 目标文件路径
            Path filePath = Paths.get(UPLOAD_DIR, newFileName);

            // 保存文件到本地
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回文件访问地址
            String fileUrl = "http://localhost:8080/uploads/" + newFileName;

            return Response.success(new ResponseData(fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("上传失败");
        }
    }

    // 用于返回文件地址的数据结构
    static class ResponseData {
        private String url;

        public ResponseData(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
