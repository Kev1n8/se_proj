package com.codeisright.attendance.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class QRCodeUtils {

    // 生成 QR 对应的String
    public static String generateQRCode(String data, Long secondsDelay) {
        // 生成随机前缀，前13位为secondsDelay秒后的时间戳
        String randomPrefix = String.valueOf(System.currentTimeMillis() + secondsDelay * 1000);
        data = randomPrefix + data;
        //
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static boolean qrInTime(String QRCode) {
        String data = new String(Base64.getDecoder().decode(QRCode));
        String randomPrefix = data.substring(0, 13);
        long timestamp = Long.parseLong(randomPrefix);
        return System.currentTimeMillis() < timestamp;
    }

    public static String getMetaId(String QRCode) {
        String data = new String(Base64.getDecoder().decode(QRCode));
        return data.substring(13);
    }
}
