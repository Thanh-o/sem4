package com.example.viewmodel;

import com.example.dao.*;
import com.example.model.*;
import com.example.util.EmailUtil;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Messagebox;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateDocumentViewModel {

    @Getter @Setter
    private String title = "";
    @Getter @Setter
    private String content = "";
    @Getter @Setter
    private String documentType = "DI"; // Mặc định
    @Getter @Setter
    private String address = "";

    @Getter
    private List<String> uploadedFiles = new ArrayList<>();
    @Getter
    private List<String> uploadedFileNames = new ArrayList<>();

    private DocumentDAO documentDAO = new DocumentDAO();
    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private WorkflowDAO workflowDAO = new WorkflowDAO();
    private UserDAO userDAO = new UserDAO();
    private User currentUser;
    @Getter private List<Media> uploadedMedias = new ArrayList<>();
    private DocumentAttachmentDAO attachmentDAO = new DocumentAttachmentDAO();

    @Init
    public void init() {
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
        }
    }

    @Command
    @NotifyChange({"uploadedMedias", "uploadedFileNames"})
    public void uploadFiles() {
        Media[] medias = Fileupload.get(10);
        if (medias != null) {
            for (Media media : medias) {
                uploadedMedias.add(media); // lưu danh sách Media để xử lý sau
                uploadedFileNames.add(media.getName()); // Lưu tên để hiển thị

            }
        }
    }

    @Command
    @NotifyChange("uploadedFileNames")
    public void removeFile(@BindingParam("filename") String filename) {
        uploadedFileNames.remove(filename);

        // Optional: nếu muốn xóa khỏi uploadedMedias luôn
        uploadedMedias.removeIf(media -> media.getName().equals(filename));
    }

    @Command
    @NotifyChange("content")
    public void updateContent(@BindingParam("content") String htmlContent) {
        System.out.println(">> Gọi updateContent()");
        if (htmlContent == null) {
            System.out.println(">> content null");
        } else {
            System.out.println(">> Nội dung CKEditor: " + htmlContent);
        }
        this.content = htmlContent;
    }

    @Command
    public void saveDocument() {
        if (title.trim().isEmpty()) {
            showMessage("Vui lòng nhập tiêu đề!");
            return;
        }
        if (content.trim().isEmpty()) {
            showMessage("Vui lòng nhập nội dung!");
            return;
        }
        if (address.trim().isEmpty()) {
            String fieldName = "DI".equals(documentType) ? "nơi nhận" : "nơi gửi";
            showMessage("Vui lòng nhập " + fieldName + "!");
            return;
        }

        Document doc = new Document(title, content, documentType, currentUser.getId());
        doc.setAddress(address);
        doc.setAttachment(String.join(";", uploadedFiles));
        doc.setStatus("CHO_XU_LY");

        boolean created = documentDAO.createDocument(doc);
        if (created) {
            // lưu file đính kèm
            String uploadDir = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/uploads");
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (Media media : uploadedMedias) {
                String serverFileName = UUID.randomUUID() + "_" + media.getName();
                File dest = new File(dir, serverFileName);

                try (InputStream in = media.getStreamData();
                     OutputStream out = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }

                    DocumentAttachment att = new DocumentAttachment();
                    att.setDocumentId(doc.getId());
                    att.setFilename(serverFileName);
                    att.setOriginalName(media.getName());
                    att.setUploadedBy(currentUser.getId());
                    attachmentDAO.save(att);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // (phần gán người xử lý tiếp theo giữ nguyên)
        }

        if (created) {
            DocumentHistory history = new DocumentHistory(
                    doc.getId(), currentUser.getId(), "TAO_MOI", "Tạo văn bản mới"
            );
            historyDAO.createHistory(history);

            // Gán người xử lý theo workflow
            List<WorkflowStep> steps = workflowDAO.getWorkflow();
            if (steps.size() > 1) {
                WorkflowStep nextStep = steps.get(1);
                User nextUser = userDAO.findUserByRole(nextStep.getRoleCode());
                int duration = nextStep.getDurationDays() != null ? nextStep.getDurationDays() : 2;
                Timestamp deadline = new Timestamp(System.currentTimeMillis() + duration * 24L * 60 * 60 * 1000);

// Tạo lịch sử xử lý gắn deadline
                DocumentHistory autoHistory = new DocumentHistory(
                        doc.getId(), nextUser.getId(), "CHUYEN_TIEP", "Tự động giao sau khởi tạo"
                );
                autoHistory.setDeadline(deadline);
                historyDAO.createHistory(autoHistory);

                if (nextUser != null) {
                    doc.setAssignedTo(nextUser.getId());
                    doc.setStatus("DANG_XU_LY");
                    documentDAO.updateDocument(doc);

                    try {
                        EmailUtil.sendEmail(
                                nextUser.getEmail(),
                                "Bạn được giao xử lý văn bản",
                                "Bạn có một văn bản mới cần xử lý: \"" + doc.getTitle() + "\"."
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            new AuditLogDAO().log(currentUser.getId(), "TAO_VAN_BAN", "Tạo văn bản: " + doc.getTitle());

            showMessage("Tạo văn bản thành công!");
            Executions.sendRedirect("/main_layout.zul");
        } else {
            showMessage("Có lỗi xảy ra khi tạo văn bản!");
        }
    }

    @Command
    public void cancel() {
        Executions.sendRedirect("/main_layout.zul");
    }

    @Command
    @NotifyChange({"addressLabel", "addressPlaceholder"})
    public void changeDocumentType() {
        // MVVM binding tự động cập nhật placeholder và nhãn
    }

    private void showMessage(String msg) {
        Messagebox.show(msg, "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
    }
    public String getAddressLabel() {
        return "DI".equals(documentType) ? "Nơi nhận:" : "Nơi gửi:";
    }
    public String getAddressPlaceholder() {
        return "DI".equals(documentType) ? "Nhập nơi nhận" : "Nhập nơi gửi";
    }

}
