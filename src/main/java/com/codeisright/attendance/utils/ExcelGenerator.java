package com.codeisright.attendance.utils;

import com.codeisright.attendance.data.AttendanceMeta;
import com.codeisright.attendance.view.StudentInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelGenerator {
    public static void save(String path, List<AttendanceMeta> metas, List<List<List<StudentInfo>>> records){
        Workbook workbook = new XSSFWorkbook();
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
                    row.createCell(0).setCellValue(students.get(k).getId());
                    row.createCell(1).setCellValue(students.get(k).getName());
                    row.createCell(i + 2).setCellValue(j == 0 ? "√" : "×");
                }
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getExcel(String path) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Workbook workbook = new XSSFWorkbook(path);
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
