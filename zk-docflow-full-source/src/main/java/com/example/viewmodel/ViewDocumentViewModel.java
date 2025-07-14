package com.example.viewmodel;

import com.example.dao.AuditLogDAO;
import com.example.dao.DocumentAttachmentDAO;
import com.example.dao.DocumentHistoryDAO;
import com.example.model.Document;
import com.example.model.DocumentAttachment;
import com.example.model.DocumentHistory;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.io.File;
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

    @Getter private List<DocumentAttachment> attachments;
    private DocumentAttachmentDAO attachmentDAO = new DocumentAttachmentDAO();
    private User currentUser;


    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    @Init
    public void init() {
        this.currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");

        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }

        selectedDocument = (Document) Sessions.getCurrent().getAttribute("selectedDocument");
        if (selectedDocument == null) {
            Executions.sendRedirect("/main_layout.zul");
            return;
        }
        new AuditLogDAO().log(currentUser.getId(), "XEM_CHI_TIET", "Xem văn bản: " + selectedDocument.getTitle());

        loadDocumentDetails();
        loadTimeline();
        loadAttachments();

    }
    private void loadAttachments() {
        attachments = attachmentDAO.getByDocumentId(selectedDocument.getId());
    }
    public boolean isUploader(DocumentAttachment a) {
        return a.getUploadedBy() == currentUser.getId();
    }

    public String getDownloadLink(DocumentAttachment att) {
        return "/uploads/" + att.getFilename();
    }

    private String getUploadBasePath() {
        return Executions.getCurrent().getDesktop().getWebApp().getRealPath("/uploads");
    }

    @Command
    @NotifyChange("attachments")
    public void deleteAttachment(@BindingParam("id") int id) {
        DocumentAttachment att = attachmentDAO.findById(id);
        if (att != null && att.getUploadedBy() == currentUser.getId()) {
            attachmentDAO.deleteById(id);
            String uploadBasePath = getUploadBasePath();
            File f = new File(uploadBasePath, att.getFilename());
            if (f.exists()) f.delete();

        }
        loadAttachments();
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
