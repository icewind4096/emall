package com.windvalley.emall.service.impl;

import com.google.common.collect.Lists;
import com.windvalley.emall.service.IFileService;
import com.windvalley.emall.util.FTPUtil;
import com.windvalley.emall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Service
@Slf4j
public class FileService implements IFileService {
    @Override
    public String upload(MultipartFile file, String path) {
    //建立上传文件存放路径
        makeDirectory(path);
    //此时文件已经上传到服务的临时目录中，后缀名为.tmp
        File uploadFile = saveFileToWebServer(file, path);
        if (uploadFile != null) {
    //把保存到web服务器的上传目录中的文件，上传到ftp服务器的image目录中
            boolean upload2Ftp = translateFileToFTPServer(getFTPServerWorkDir(), Lists.newArrayList(uploadFile));
    //不管文件上传到ftp服务器是否成功,都把保存到web服务器的上传目录中的文件删除
            deleteFileInWebServer(uploadFile);
            if (upload2Ftp == true) {
            //返回上传文件名
                return uploadFile.getName();
            }
        }
        return null;
    }

    @Override
    public String upload(File file) {
    //把保存到web服务器的上传目录中的文件，上传到ftp服务器的image目录中
        boolean upload2Ftp = translateFileToFTPServer(getFTPServerWorkDir(), Lists.newArrayList(file));
    //不管文件上传到ftp服务器是否成功,都把保存到web服务器的上传目录中的文件删除
        //deleteFileInWebServer(file);
        if (upload2Ftp == true) {
            //返回上传文件名
            return file.getName();
        }
        return null;
    }

    private String getFTPServerWorkDir() {
        return PropertiesUtil.getProperty("ftp.server.workdir");
    }

    private void deleteFileInWebServer(File file) {
        file.delete();
    }

    private boolean translateFileToFTPServer(String remotePath, ArrayList<File> files) {
        return FTPUtil.uploadFile(remotePath, Lists.newArrayList(files));
    }

    private File saveFileToWebServer(MultipartFile file, String path) {
        String originalFileName = file.getOriginalFilename();
    //把原始文件名修改为随机文件名，防止与已在服务器上文件重名
        String uploadFileName = getUploadFileName(getFileExtName(originalFileName));
        log.info("开始上传文件： 源文件名->{} 上传路径->{} 新文件名->{}", originalFileName, path, uploadFileName);
    //检查WEB服务器存放目录是否存在, 不存在，就建立目录
        log.info("开始上传文件 建立目录：{}", path);
    //把上传的临时文件，保存到web服务器的上传目录中
        log.info("开始上传文件 保存文件到web服务器的上传目录：{}", path);
        return saveToFile(file, path, uploadFileName);
    }

    private File saveToFile(MultipartFile file, String path, String uploadFileName) {
        File uploadFile = new File(path, uploadFileName);
        try {
            file.transferTo(uploadFile);
            log.info("开始上传文件 保存文件到web服务器的上传目录->成功");
            return uploadFile;
        } catch (IOException e) {
            log.info("开始上传文件 保存文件到web服务器的上传目录->失败", e);
            return null;
        }
    }

    private String getUploadFileName(String fileExtName) {
        return String.format("%s.%s", UUID.randomUUID().toString(), fileExtName);
    }

    private void makeDirectory(String path) {
        File directory = new File(path);
        if (directory.isDirectory() == false){
            directory.setWritable(true);
            directory.mkdirs();
        }
    }

    private String getFileExtName(String fileName) {
        String[] lists = fileName.split("\\.");
        return lists[lists.length - 1];
    }
}
