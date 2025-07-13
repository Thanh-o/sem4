package com.example.viewmodel;

import com.example.dao.DocumentDAO;
import com.example.model.Document;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Filedownload;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardViewModel {

    @Getter @Setter private Date fromDate = new Date();
    @Getter @Setter private Date toDate = new Date();
    @Getter @Setter private String selectedStatus;
    @Getter private List<Map.Entry<String, Integer>> stats;
    @Getter private List<String> statusList;

    private DocumentDAO documentDAO = new DocumentDAO();

    @Init
    public void init() {
        User user = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (user == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }

        statusList = Arrays.asList("Tất cả", "CHO_XU_LY", "DANG_XU_LY", "HOAN_THANH", "TU_CHOI");
        loadStatistics();
    }

    @Command
    @NotifyChange("stats")
    public void loadStatistics() {
        Map<String, Integer> map = documentDAO.getDocumentStatistics();
        stats = new ArrayList<>(map.entrySet());
    }

    @Command
    public void generatePdfReport() throws Exception {
        java.sql.Date sqlFrom = new java.sql.Date(fromDate.getTime());
        java.sql.Date sqlTo = new java.sql.Date(toDate.getTime());

        String status = "Tất cả".equals(selectedStatus) ? null : selectedStatus;
        List<Document> documents = documentDAO.getDocumentsByDateAndStatus(sqlFrom, sqlTo, status);

        InputStream reportStream = Executions.getCurrent().getDesktop().getWebApp()
                .getResourceAsStream("/reports/document_report.jrxml");

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(documents);

        Map<String, Object> params = new HashMap<>();
        params.put("reportTitle", "BÁO CÁO THỐNG KÊ VĂN BẢN");
        params.put("dateRange", "Từ " + formatDate(fromDate) + " đến " + formatDate(toDate));
        params.put("statusFilter", status != null ? status : "Tất cả");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
        Filedownload.save(pdf, "application/pdf", "baocao_vanban.pdf");
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    @Command
    public void openAdvancedSearch() {
        Executions.sendRedirect("/advanced-search.zul");
    }


}
