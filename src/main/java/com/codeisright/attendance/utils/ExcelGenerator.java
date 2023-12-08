package com.codeisright.attendance.utils;

import com.codeisright.attendance.data.AttendanceMeta;
import com.codeisright.attendance.view.StudentInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ExcelGenerator.class);

    public static byte[] save(String path, List<AttendanceMeta> metas, List<List<List<StudentInfo>>> records){
        Workbook workbook = new XSSFWorkbook();// will save as .xlsx
        Sheet sheet = workbook.createSheet("Attendance");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Student ID");
        row.createCell(1).setCellValue("Student Name");
        for (int i = 0; i < metas.size(); i++) {
            row.createCell(i + 2).setCellValue("签到" + (i + 1));
        }
        for (int i = 0; i < records.size(); i++) {
            List<List<StudentInfo>> circumstance = records.get(i);
            for (int j = 0; j < circumstance.size(); j++) {
                List<StudentInfo> students = circumstance.get(j);
                for (int k = 0; k < students.size(); k++) {
                    row = sheet.createRow(k + 1);
                    row.createCell(0).setCellValue(students.get(k).getId()); // 有点冗余，但是不想改了
                    row.createCell(1).setCellValue(students.get(k).getName());
                    row.createCell(i + 2).setCellValue(j == 0 ? "√" : "×");
                }
            }
            // 创建到课率
            row = sheet.createRow(sheet.getLastRowNum() + 2);
            row.createCell(0).setCellValue("到课率");
//            row.createCell(2).setCellValue(metas.get(i).getRate());
        }
        logger.info("Saving attendance to " + path);
        byte[] toReturn = new byte[0];
        try (FileOutputStream fileOutputStream = new FileOutputStream(path, false);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(fileOutputStream);
            workbook.write(byteArrayOutputStream);
            toReturn = byteArrayOutputStream.toByteArray();
            logger.info("File saved to " + path);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
