package com.bbsserver.common.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Service
@Log4j2
public class ImageService {

    @Value("${file.image.path}")
    private String imagePath;
    @Value("${file.image.tpath}")
    private String imagetPath;
    /**
     * 图片上传
     * @param file
     **/
    public String upload(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String newName = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
        //file.transferTo(new File(imagePath, newName));
        Files.copy(file.getInputStream(), Paths.get(imagePath + newName));
        Files.copy(file.getInputStream(), Paths.get(imagetPath + newName));
        return newName;
    }

    @PostConstruct
    public void init() {
        log.info("图片路径:{}", imagePath);
        File file = new File(imagePath);
        if (!file.exists()) {
            log.info("图片路径不存在");
            if (file.mkdir()) {
                log.info("图片路径创建成功");
            } else {
                log.error("图片路径创建失败");
            }
        } else {
            log.info("图片路径存在");
        }
    }
}
