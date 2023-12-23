package com.codeisright.attendance.utils;

import com.codeisright.attendance.data.AttendanceMeta;
import com.codeisright.attendance.view.StudentInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.*;

public class ExcelHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExcelHandler.class);

    public static byte[] save(String path, List<AttendanceMeta> metas, List<List<List<StudentInfo>>> records){
        Workbook workbook = new XSSFWorkbook();// will save as .xlsx
        Sheet sheet = workbook.createSheet("Attendance");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Student ID");
        row.createCell(1).setCellValue("Student Name");

        for (int i = 0; i < metas.size(); i++) {
            row.createCell(i + 2).setCellValue("签到" + (i + 1));
        }

        TreeMap<String, List<String>> toWrite = new TreeMap<>();
        HashMap<String, String> idToName = new HashMap<>();

        for (List<List<StudentInfo>> circumstance : records) {  // 第几次签到
            for (int j = 0; j < circumstance.size(); j++) {  // 第几种情况：成功、失败、补签
                List<StudentInfo> students = circumstance.get(j);
                for (StudentInfo student : students) { // 第几个学生
                    String id = student.getId();
                    if (toWrite.get(id) == null) {
                        idToName.put(id, student.getName());
                        toWrite.put(id, new ArrayList<>(List.of(switch (j) {
                            case 0 -> "√";
                            case 1 -> "x";
                            case 2 -> "补签";
                            default -> "unknown";
                        })));
                    }
                    else{
                        toWrite.get(id).add(switch (j){
                            case 0 -> "√";
                            case 1 -> "x";
                            case 2 -> "补签";
                            default -> "unknown";
                        });
                    }
                }
            }
        }

        int rowIndex = 1;
        Vector<Integer> count = new Vector<>();
        count.setSize(records.size());
        Collections.fill(count, 0);
        for (String id : toWrite.keySet()) {
            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(idToName.get(id));
            List<String> attendance = toWrite.get(id);
            for (int i = 0; i < attendance.size(); i++) {
                String status = attendance.get(i);
                row.createCell(i + 2).setCellValue(status);
                if(status.equals("x")){
                    count.set(i, count.get(i) + 1);
                }
            }
        }

        rowIndex++;
        row = sheet.createRow(rowIndex);
        row.createCell(1).setCellValue("到课率");
        for (int i = 0; i < count.size(); i++) {
            String num = String.format("%.2f", (1 - count.get(i) / (double) toWrite.size())*100);
            row.createCell(i + 2).setCellValue(num+"%");
            logger.info("Attendance rate of " + (i + 1) + " is " + num);
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

    public static List<String> getStudentIds(byte[] excelFile){
        List<String> list = new ArrayList<>();
        ByteArrayInputStream content = new ByteArrayInputStream(excelFile);
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(content);  // will read .xlsx
        }catch (Exception e){
            logger.error("Error occurred when parsing excel file", e);
        }
        assert workbook != null;
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            if (sheet.getRow(i).getCell(i).getStringCellValue().equals("学号")){
                for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                    String type = sheet.getRow(j).getCell(i).getCellType().toString();
                    switch (type) {
                        case "STRING" -> list.add(sheet.getRow(j).getCell(i).getStringCellValue());
                        case "NUMERIC" -> list.add(String.valueOf((int) sheet.getRow(j).getCell(i).getNumericCellValue()));
                        default -> logger.error("Unknown type: " + type);
                    }
                }
            }
        }
        return list;
    }
}
