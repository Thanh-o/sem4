package com.example.viewmodel;

import com.example.dao.*;
import com.example.model.*;
import com.example.util.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.sql.Timestamp;
import java.util.List;

public class ProcessDocumentViewModel {

    private DocumentDAO documentDAO = new DocumentDAO();
    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private UserDAO userDAO = new UserDAO();
    private WorkflowDAO workflowDAO = new WorkflowDAO();

    @Getter private Document selectedDocument;
    @Getter private User currentUser;

    @Getter @Setter private String title;
    @Getter @Setter private String documentType;
    @Getter @Setter private String content;
    @Getter @Setter private String comment;

    @Getter private List<User> employees;
    @Getter @Setter private User selectedAssignee;

    @Init
    public void init() {
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        selectedDocument = (Document) Sessions.getCurrent().getAttribute("selectedDocument");

        if (currentUser == null || selectedDocument == null) {
            Executions.sendRedirect("/main_layout.zul");
            return;
        }

        title = selectedDocument.getTitle();
        documentType = selectedDocument.getDocumentTypeDisplay();
        content = selectedDocument.getContent();

        employees = userDAO.getAllUsers();
    }

    @Command
    public void approve() {
        List<WorkflowStep> steps = workflowDAO.getWorkflow();
        String currentRole = currentUser.getRole();
        int currentIndex = -1;

        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).getRoleCode().equalsIgnoreCase(currentRole)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            Messagebox.show("Không tìm thấy bước hiện tại trong workflow!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        boolean isLast = (currentIndex == steps.size() - 1)
                || "KET_THUC".equalsIgnoreCase(steps.get(currentIndex + 1).getRoleCode());

        if (isLast) {
            selectedDocument.setStatus("HOAN_THANH");
            selectedDocument.setAssignedTo(null);
            documentDAO.updateDocument(selectedDocument);
            saveHistory("PHE_DUYET", "Phê duyệt và hoàn thành: " + comment); // ✅ dùng saveHistory ở đây
            new AuditLogDAO().log(currentUser.getId(), "PHE_DUYET", "Phê duyệt văn bản: " + selectedDocument.getTitle());

        } else {
            WorkflowStep nextStep = steps.get(currentIndex + 1);
            User nextUser = userDAO.findRandomUserByRole(nextStep.getRoleCode());

            if (nextUser == null) {
                Messagebox.show("Không tìm thấy người xử lý cho vai trò: " + nextStep.getRoleCode(),
                        "Lỗi", Messagebox.OK, Messagebox.ERROR);
                return;
            }

            assignWithDeadline(nextUser, "PHE_DUYET",
                    "Phê duyệt và chuyển tiếp đến: " + nextUser.getFullName()
                            + " (" + nextUser.getRole() + ") - Ghi chú: " + comment);

            try {
                EmailUtil.sendEmail(nextUser.getEmail(), "Bạn được giao xử lý văn bản",
                        "Bạn vừa nhận một văn bản cần xử lý: " + selectedDocument.getTitle());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }


        Executions.sendRedirect("/main_layout.zul");
    }

    @Command
    public void forward() {
        if (selectedAssignee == null) {
            Messagebox.show("Vui lòng chọn người xử lý tiếp theo!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        selectedDocument.setAssignedTo(selectedAssignee.getId());
        selectedDocument.setStatus("DANG_XU_LY");
        documentDAO.updateDocument(selectedDocument);

        assignWithDeadline(selectedAssignee, "CHUYEN_TIEP", "Chuyển tiếp: " + comment);


        try {
            EmailUtil.sendEmail(selectedAssignee.getEmail(),
                    "Văn bản mới được chuyển đến",
                    "Bạn vừa nhận được một văn bản mới: " + selectedDocument.getTitle());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        new AuditLogDAO().log(currentUser.getId(), "CHUYEN_TIEP", "Chuyển tiếp văn bản: " + selectedDocument.getTitle());

        Executions.sendRedirect("/main_layout.zul");
    }

    @Command
    public void reject() {
        selectedDocument.setStatus("TU_CHOI");
        selectedDocument.setAssignedTo(null);
        documentDAO.updateDocument(selectedDocument);
        saveHistory("TU_CHOI", "Từ chối: " + comment);
        new AuditLogDAO().log(currentUser.getId(), "TU_CHOI", "Từ chối văn bản: " + selectedDocument.getTitle());

        Executions.sendRedirect("/main_layout.zul");
    }

    @Command
    public void goBack() {
        Executions.sendRedirect("/main_layout.zul");
    }

    private void saveHistory(String action, String note) {
        DocumentHistory history = new DocumentHistory(selectedDocument.getId(), currentUser.getId(), action, note);
        historyDAO.createHistory(history);
    }
    private void assignWithDeadline(User toUser, String action, String note) {
        selectedDocument.setAssignedTo(toUser.getId());
        selectedDocument.setStatus("DANG_XU_LY");
        documentDAO.updateDocument(selectedDocument);

        // 🔁 Reset deadline mỗi khi chuyển xử lý
        WorkflowStep currentStep = workflowDAO.getStepByRole(toUser.getRole());
        int days = currentStep != null && currentStep.getDurationDays() != null
                ? currentStep.getDurationDays() : 2;
        Timestamp deadline = new Timestamp(System.currentTimeMillis() + days * 24L * 60 * 60 * 1000);

        DocumentHistory history = new DocumentHistory(selectedDocument.getId(), toUser.getId(), action, note);
        history.setDeadline(deadline);
        historyDAO.createHistory(history);
    }

}
