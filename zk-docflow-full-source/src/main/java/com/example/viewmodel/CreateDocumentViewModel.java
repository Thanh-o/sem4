package com.example.viewmodel;

import com.example.dao.DocumentDAO;
import com.example.dao.DocumentHistoryDAO;
import com.example.dao.UserDAO;
import com.example.dao.WorkflowDAO;
import com.example.model.Document;
import com.example.model.DocumentHistory;
import com.example.model.User;
import com.example.model.WorkflowStep;
import com.example.util.EmailUtil;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.io.*;
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

    @Init
    public void init() {
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
        }
    }

    @Command
    @NotifyChange({"uploadedFiles", "uploadedFileNames"})
    public void uploadFiles() {
        Media[] medias = org.zkoss.zul.Fileupload.get(10); // Cho phép chọn tối đa 10 file
        if (medias != null) {
            for (Media media : medias) {
                try {
                    String uploadDir = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/uploads");
                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    String filename = UUID.randomUUID() + "_" + media.getName();
                    File file = new File(dir, filename);

                    try (InputStream in = media.getStreamData();
                         OutputStream out = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }

                    uploadedFiles.add(filename);
                    uploadedFileNames.add(media.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
            DocumentHistory history = new DocumentHistory(
                    doc.getId(), currentUser.getId(), "TAO_MOI", "Tạo văn bản mới"
            );
            historyDAO.createHistory(history);

            // Gán người xử lý theo workflow
            List<WorkflowStep> steps = workflowDAO.getWorkflow();
            if (steps.size() > 1) {
                WorkflowStep nextStep = steps.get(1);
                User nextUser = userDAO.findUserByRole(nextStep.getRoleCode());

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

            showMessage("Tạo văn bản thành công!");
            Executions.sendRedirect("/main.zul");
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
