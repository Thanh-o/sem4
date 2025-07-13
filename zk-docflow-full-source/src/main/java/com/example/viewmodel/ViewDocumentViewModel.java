package com.example.viewmodel;

import com.example.dao.DocumentHistoryDAO;
import com.example.model.Document;
import com.example.model.DocumentHistory;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import java.text.SimpleDateFormat;
import java.util.List;

public class ViewDocumentViewModel {

    @Getter
    private Document selectedDocument;

    @Getter
    private List<DocumentHistory> timeline;

    @Getter @Setter
    private String title;
    @Getter @Setter
    private String documentType;
    @Getter @Setter
    private String status;
    @Getter @Setter
    private String createdBy;
    @Getter @Setter
    private String assignedTo;
    @Getter @Setter
    private String address;
    @Getter @Setter
    private String createdAt;
    @Getter @Setter
    private String content;
    @Getter @Setter
    private String attachmentName;
    @Getter @Setter
    private String attachmentHref;
    @Getter
    private String remainingTimeDisplay;

    @Getter
    private boolean overdue;



    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Init
    public void init() {
        User currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }

        selectedDocument = (Document) Sessions.getCurrent().getAttribute("selectedDocument");
        if (selectedDocument == null) {
            Executions.sendRedirect("/main.zul");
            return;
        }

        loadDocumentDetails();
        loadTimeline();
    }

    private void loadDocumentDetails() {
        title = selectedDocument.getTitle();
        documentType = selectedDocument.getDocumentTypeDisplay();
        status = selectedDocument.getStatusDisplay();
        createdBy = selectedDocument.getCreatedByName();
        address = selectedDocument.getAddress();
        assignedTo = selectedDocument.getAssignedToName() != null ? selectedDocument.getAssignedToName() : "Chưa giao";
        createdAt = dateFormat.format(selectedDocument.getCreatedAt());
        content = selectedDocument.getContent();

        if (selectedDocument.getAttachment() != null && !selectedDocument.getAttachment().isEmpty()) {
            attachmentName = "Tải xuống";
            attachmentHref = "/uploads/" + selectedDocument.getAttachment();
        } else {
            attachmentName = "Không có tệp";
            attachmentHref = null;
        }
        if (selectedDocument.getDeadline() != null) {
            long millisLeft = selectedDocument.getDeadline().getTime() - System.currentTimeMillis();
            if (millisLeft <= 0) {
                overdue = true;
                remainingTimeDisplay = "⚠️ Quá hạn";
            } else {
                long hours = millisLeft / (1000 * 60 * 60);
                long minutes = (millisLeft / (1000 * 60)) % 60;
                remainingTimeDisplay = String.format("%02dh%02dm", hours, minutes);
            }
        } else {
            overdue = true;
            remainingTimeDisplay = "—";
        }

    }

    private void loadTimeline() {
        timeline = historyDAO.getHistoryByDocumentId(selectedDocument.getId());
    }
    public String displayLine(DocumentHistory item) {
        if (item == null) return "";
        return item.getTimeDisplay() + (item.getComment() != null && !item.getComment().isEmpty() ? " - " + item.getComment() : "");
    }

    @Command
    public void goBack() {
        Executions.sendRedirect("/main_layout.zul");
    }


}
