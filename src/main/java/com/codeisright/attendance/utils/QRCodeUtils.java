package com.codeisright.attendance.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtils {

    // 生成 QR Code
    public static String generateQRCode(String data) {
        try {
            // 生成随机前缀，前13位为30秒后的时间戳
            String randomPrefix = String.valueOf(System.currentTimeMillis() + 30000);
            data = randomPrefix + data;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // 将生成的 QR Code 转换为 Base64 字符串
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 扫描 QR Code
    public static String scanQRCode(String imagePath) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
            LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
            Binarizer binarizer = new HybridBinarizer(luminanceSource);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeReader qrCodeReader = new QRCodeReader();
            Result result = qrCodeReader.decode(binaryBitmap, hints);

            return result.getText().substring(13);
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ChecksumException e) {
            throw new RuntimeException(e);
        } catch (FormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean qrInTime(String QRCode) {
        String data = new String(Base64.getDecoder().decode(QRCode));
        String randomPrefix = data.substring(0, 13);
        long timestamp = Long.parseLong(randomPrefix);
        return System.currentTimeMillis() < timestamp;
    }

    public static boolean isMetaIdEqual(String metaId, String QRCode) {
        String data = new String(Base64.getDecoder().decode(QRCode));
        String metaIdInQRCode = data.substring(13, 13 + metaId.length());
        return metaId.equals(metaIdInQRCode);
    }
}
