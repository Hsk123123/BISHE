package com.skillchain.controller;

import com.skillchain.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.base-url}")
    private String baseUrl;

    @PostMapping("/upload")
    public ResponseEntity<Result<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Result.error("文件不能为空"));
            }

            // 获取文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);

            // 生成唯一文件名
            String newFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // 创建文件存储目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 创建目标文件
            File dest = new File(uploadDir, newFilename);

            // 保存文件
            file.transferTo(dest);

            // 构建文件访问URL
            String fileUrl = baseUrl + "/" + newFilename;

            return ResponseEntity.ok(Result.success(fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Result.error("文件上传失败: " + e.getMessage()));
        }
    }
}
