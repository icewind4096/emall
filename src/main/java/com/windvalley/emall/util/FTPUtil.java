package com.windvalley.emall.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Data
@Slf4j
public class FTPUtil {
    private static String FTPIP = PropertiesUtil.getProperty("ftp.server.ip");
    private static String FTPUser = PropertiesUtil.getProperty("ftp.user");
    private static String FTPPassword = PropertiesUtil.getProperty("ftp.pass");
    private static Integer FTPPort = Integer.parseInt(PropertiesUtil.getProperty("ftp.server.port", "21"));

    private String ip;
    private Integer port;
    private String userName;
    private String password;
    private FTPClient ftpClient;

    public static boolean uploadFile(String remotePath, List<File> fileList){
        FTPUtil ftpUtil = new FTPUtil(FTPIP, FTPPort, FTPUser, FTPPassword);
        return ftpUtil.uploadFiles(remotePath, fileList);
    }

    private boolean uploadFiles(String remotePath, List<File> fileList){
        boolean success = true;
        if (connectFTPServer(ip, port, userName, password) == true){
            for (File file: fileList){
                setFTPConfig(remotePath);
                if (translateFile(file) == false){
                    success = false;
                    break;
                }
            }
        }
        disConnectFTPServer();
        return success;
    }

    private void disConnectFTPServer() {
        try {
            log.error("开始登出ftp服务器");
            ftpClient.logout();
            log.error("登出ftp服务器成功");
        } catch (IOException e) {
            log.error("登出ftp服务器失败", e);
        }finally {
            try {
                log.error("开始断开ftp服务器");
                ftpClient.disconnect();
                log.error("开始断开ftp服务器->成功");
            } catch (IOException e) {
                log.error("开始断开ftp服务器->失败", e);
            }
        }
    }

    private boolean translateFile(File file) {
        log.info("开始上传ftp服务器文件->{}", file.getName());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            if (ftpClient.storeFile(file.getName(), fis)){
                log.info("上传ftp服务器文件成功->{}", file.getName());
                return true;
            } else {
                log.info("上传ftp服务器文件失败");
                return false;
            }
        } catch (IOException e) {
            log.info("上传ftp服务器文件失败 ", e);
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("上传ftp服务器文件，关闭流异常", e);
                }
            }
        }
    }

    private void setFTPConfig(String remotePath) {
        log.info("开始设置FTP服务器");
        try {
        //传输文件类型设置为二进制
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        //传输文件类型编码方式
            ftpClient.setControlEncoding("UTF-8");
            //改变工作目录
            log.info("设置FTP服务器 修改工作目录");
            ftpClient.changeWorkingDirectory(remotePath);
            log.info("设置FTP服务器 修改工作目录成功->{}", remotePath);
            log.info("设置FTP服务器成功");
        //设置缓冲区大小
            ftpClient.setBufferSize(1024);
        //设置访问被动模式
            ftpClient.enterLocalPassiveMode();
        } catch (IOException e) {
            log.error("设置FTP服务器->失败", e);
        }
    }

    private boolean connectFTPServer(String ip, Integer port, String userName, String password) {
        ftpClient = new FTPClient();
        try {
            //设置传输超时
            ftpClient.setDataTimeout(2000);
            //设置连接超时
            ftpClient.setConnectTimeout(2000);
            log.info("开始连接FTP服务器");
            ftpClient.connect(ip, port);
            log.info("开始登录FTP服务器");
            ftpClient.login(userName, password);
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                log.info("登录FTP服务器->成功");
                return true;
            }else {
                log.info("登录FTP服务器->失败");
            }
        } catch (IOException e) {
            log.error("连接FTP服务器异常", e);
        }
        return false;
    }

    public FTPUtil(String ip, Integer port, String userName, String password){
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }
}
