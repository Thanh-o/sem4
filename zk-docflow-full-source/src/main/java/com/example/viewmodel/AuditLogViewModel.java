package com.example.viewmodel;

import com.example.dao.AuditLogDAO;
import com.example.model.AuditLog;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.apache.poi.ss.usermodel.Row;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AuditLogViewModel {

    @Getter
    private List<AuditLog> logs;
    private AuditLogDAO logDAO = new AuditLogDAO();

    @Init
    public void init() {
        logs = logDAO.getAllLogs();
    }

    @Command
    public void exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Audit Log");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Người thực hiện");
            header.createCell(1).setCellValue("Hành động");
            header.createCell(2).setCellValue("Mô tả");
            header.createCell(3).setCellValue("Thời gian");

            int rowIdx = 1;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (AuditLog log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getUserName());
                row.createCell(1).setCellValue(log.getActionType());
                row.createCell(2).setCellValue(log.getDescription());
                row.createCell(3).setCellValue(sdf.format(log.getActionTime()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            Filedownload.save(out.toByteArray(),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "audit_log.xlsx");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Command
    public void goBack() {
        Executions.sendRedirect("/main_layout.zul");
    }


}

