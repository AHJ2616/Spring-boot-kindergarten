package com.kinder.kindergarten.service.money;

import com.kinder.kindergarten.DTO.money.MoneyFormDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportMoneyListToExcel(List<MoneyFormDTO> moneyList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Money Management");

            // 헤더 스타일 설정
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"날짜", "구분", "금액", "내용", "사용자", "사용처"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 입력
            int rowNum = 1;
            for (MoneyFormDTO money : moneyList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(money.getMoneyUseDate().toString());
                row.createCell(1).setCellValue(money.getMoneyStatus().toString());
                row.createCell(2).setCellValue(money.getMoneyHowMuch());
                row.createCell(3).setCellValue(money.getMoneyContent());
                row.createCell(4).setCellValue(money.getMoneyWho());
                row.createCell(5).setCellValue(money.getMoneyCompany());
            }

            // 컬럼 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 엑셀 파일을 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}