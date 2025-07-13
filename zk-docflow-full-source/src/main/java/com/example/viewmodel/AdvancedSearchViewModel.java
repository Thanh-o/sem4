package com.example.viewmodel;

import com.example.dao.DocumentDAO;
import com.example.model.Document;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;

import java.util.Date;
import java.util.List;

public class AdvancedSearchViewModel {

    @Getter @Setter private Date fromDate;
    @Getter @Setter private Date toDate;
    @Getter @Setter private String lastProcessor = "";
    @Getter @Setter private String selectedStatus = "";
    @Getter @Setter private String keyword = "";

    @Getter @Setter private List<Document> resultDocuments;

    private DocumentDAO documentDAO = new DocumentDAO();
    @Init
    public void init() {
        System.out.println("✅ AdvancedSearchViewModel đã khởi tạo!");
    }


    @Command
    @NotifyChange("resultDocuments")
    public void search() {
        System.out.println("🔍 Đã vào search()");
        System.out.println("fromDate: " + fromDate);
        System.out.println("toDate: " + toDate);
        System.out.println("keyword: " + keyword);
        System.out.println("status: " + selectedStatus);
        System.out.println("lastProcessor: " + lastProcessor);

        resultDocuments = documentDAO.advancedSearch(fromDate, toDate, selectedStatus, keyword, lastProcessor);
        System.out.println("📦 Tổng kết quả: " + (resultDocuments != null ? resultDocuments.size() : -1));
    }

    @Command
    public void goBack() {
        Executions.sendRedirect("/main_layout.zul");
    }


}
