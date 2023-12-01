package com.codeisright.attendance.utils;

import javax.imageio.ImageIO;
import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageHadler {
    private String id;

    public ImageHadler(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void saveImage(byte[] image) {
        String picPath = "src/main/resources/static/images/" + id + ".jpg";
        try {
            Files.write(Paths.get(picPath), image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image getImage() {
        String picPath = "src/main/resources/static/images/" + id + ".jpg";
        byte[] buffer = ImageUtils.getImageFromPath(picPath);

        if (buffer != null) {
            try {
                // 将字节数组转换为Image对象
                return ImageIO.read(new File(picPath));
            } catch (IOException e) {
                e.printStackTrace(); // 处理读取图像文件时的异常
            }
        }

        // 当buffer为null时，返回默认图像
        String defaultPicPath = "src/main/resources/static/images/default.jpg";
        try {
            return ImageIO.read(new File(defaultPicPath));
        } catch (IOException e) {
            e.printStackTrace(); // 处理读取默认图像文件时的异常
            return null;
        }
    }

    @Override
    public String toString() {
        return "ImageHadler{" +
                "id='" + id + '\'' +
                '}';
    }
}