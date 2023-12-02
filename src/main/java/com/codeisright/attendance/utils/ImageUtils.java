package com.codeisright.attendance.utils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    public static String getBase64Image(byte[] image) {
        return Base64.getEncoder().encodeToString(image);
    }

    public static byte[] getImageFromBase64(String base64Image) {
        return Base64.getDecoder().decode(base64Image);
    }

    public static String getBase64ImageFromPath(String path) {
        return getBase64Image(getImageFromPath(path));
    }

    public static byte[] getImageFromPath(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            logger.error("Failed to read image from path: " + path, e);
        }
        return null;
    }

    public static void saveImageFromBase64(String base64Image, String path) {
        saveImage(getImageFromBase64(base64Image), path);
    }

    public static void saveImage(byte[] image, String path) {
        try {
            Files.write(Paths.get(path), image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
