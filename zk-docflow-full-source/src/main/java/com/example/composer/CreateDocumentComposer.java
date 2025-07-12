package com.example.composer;

import com.example.dao.DocumentDAO;
import com.example.dao.DocumentHistoryDAO;
import com.example.model.Document;
import com.example.model.DocumentHistory;
import com.example.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.zkoss.util.media.Media;


public class CreateDocumentComposer extends SelectorComposer<Component> {
    
    @Wire
    private Textbox txtTitle;
    
    @Wire
    private Textbox txtContent;
    
    @Wire
    private Radiogroup rgDocumentType;

    @Wire private Label lblAddress;
    @Wire private Textbox txtAddress;
    @Wire private Label lblFileName;
    @Wire private Vbox vboxFileList;

    private List<String> uploadedFiles = new ArrayList<>();

    private DocumentDAO documentDAO = new DocumentDAO();
    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private User currentUser;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }
    }

    @Listen("onClick = #btnSave")
    public void saveDocument() {
        String title = txtTitle.getValue();
        String content = txtContent.getValue();
        String documentType = rgDocumentType.getSelectedItem().getValue();
        String address = txtAddress.getValue();  // Trường động: nơi gửi / nơi nhận

        if (title == null || title.trim().isEmpty()) {
            Messagebox.show("Vui lòng nhập tiêu đề!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            Messagebox.show("Vui lòng nhập nội dung!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (address == null || address.trim().isEmpty()) {
            String fieldName = "DI".equals(documentType) ? "nơi nhận" : "nơi gửi";
            Messagebox.show("Vui lòng nhập " + fieldName + "!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        Document document = new Document(title, content, documentType, currentUser.getId());
        document.setAddress(address);  // Gán giá trị địa chỉ
        document.setAttachment(String.join(";", uploadedFiles));
        // gán file đính kèm nếu có

        if (documentDAO.createDocument(document)) {
            // Lưu lịch sử
            DocumentHistory history = new DocumentHistory(
                    document.getId(), currentUser.getId(), "TAO_MOI", "Tạo văn bản mới"
            );
            historyDAO.createHistory(history);

            Messagebox.show("Tạo văn bản thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
            Executions.sendRedirect("/main.zul");
        } else {
            Messagebox.show("Có lỗi xảy ra khi tạo văn bản!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }


    @Listen("onClick = #btnCancel")
    public void cancel() {
        Executions.sendRedirect("/main.zul");
    }

    @Listen("onCheck = #rgDocumentType")
    public void onDocumentTypeChanged() {
        String selectedType = rgDocumentType.getSelectedItem().getValue();
        if ("DI".equals(selectedType)) {
            lblAddress.setValue("Nơi nhận:");
            txtAddress.setPlaceholder("Nhập nơi nhận");
        } else {
            lblAddress.setValue("Nơi gửi:");
            txtAddress.setPlaceholder("Nhập nơi gửi");
        }
    }

    @Listen("onClick = #btnUpload")
    public void onUploadFile() {
        Media[] medias = Fileupload.get(10); // Cho phép tối đa 10 tệp
        if (medias != null) {
            for (Media media : medias) {
                try {
                    String uploadDir = Executions.getCurrent().getDesktop().getWebApp()
                            .getRealPath("/uploads");
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

                    // Hiển thị tên file trong giao diện
                    Label fileLabel = new Label(media.getName());
                    fileLabel.setStyle("padding: 2px;");
                    fileLabel.setParent(vboxFileList);

                } catch (Exception e) {
                    Messagebox.show("Lỗi tải file: " + media.getName(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
                }
            }
        }
    }

}
