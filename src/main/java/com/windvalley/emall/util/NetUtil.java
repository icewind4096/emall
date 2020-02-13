package com.windvalley.emall.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class NetUtil {
    public static String saveFileFromURL(String urlPath, String path){
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                makeDirectory(path);

                File file = new File(path, urlPath.substring(urlPath.lastIndexOf("/") + 1));

                try (InputStream is = connection.getInputStream();
                     FileOutputStream fos = new FileOutputStream(file)){
                    byte[] buffer = new byte[8192];

                    int length = -1;
                    while ((length = is.read(buffer)) != -1){
                        fos.write(buffer, 0, length);
                    }

                    fos.flush();
                }

                return file.getName();
            };
        } catch (IOException e) {
            log.error("下载远端文件出错", e);
        }
        return null;
    }

    private static void makeDirectory(String path) {
        File directory = new File(path);
        if (directory.isDirectory() == false){
            directory.setWritable(true);
            directory.mkdirs();
        }
    }
}
