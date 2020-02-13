package com.windvalley.emall.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IFileService {
    String upload(MultipartFile file, String path);
    String upload(File file);
}
