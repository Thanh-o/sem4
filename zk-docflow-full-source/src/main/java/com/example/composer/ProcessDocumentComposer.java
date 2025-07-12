package com.example.composer;

import com.example.dao.DocumentDAO;
import com.example.dao.DocumentHistoryDAO;
import com.example.dao.UserDAO;
import com.example.model.Document;
import com.example.model.DocumentHistory;
import com.example.model.User;
import com.example.util.EmailUtil;
import jakarta.mail.MessagingException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class ProcessDocumentComposer extends SelectorComposer<Component> {

    @Wire private Label lblTitle;
    @Wire private Label lblType;
    @Wire private Textbox txtContent;
    @Wire private Textbox txtComment;
    @Wire private Combobox cbAssignee;

    private Document selectedDocument;
    private User currentUser;
    private DocumentDAO documentDAO = new DocumentDAO();
    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        selectedDocument = (Document) Sessions.getCurrent().getAttribute("selectedDocument");

        if (currentUser == null || selectedDocument == null) {
            Executions.sendRedirect("/main.zul");
            return;
        }

        lblTitle.setValue(selectedDocument.getTitle());
        lblType.setValue(selectedDocument.getDocumentTypeDisplay());
        txtContent.setValue(selectedDocument.getContent());

        List<User> employees = userDAO.getAllUsers();
        for (User u : employees) {
            Comboitem item = new Comboitem(u.getFullName());
            item.setValue(u);
            item.setParent(cbAssignee);
        }
    }

    @Listen("onClick = #btnApprove")
    public void onApprove() {
        selectedDocument.setStatus("HOAN_THANH");
        selectedDocument.setAssignedTo(null); // Kết thúc xử lý

        documentDAO.updateDocument(selectedDocument);
        saveHistory("PHE_DUYET", "Phê duyệt: " + txtComment.getValue());
        Executions.sendRedirect("/main.zul");
    }

    @Listen("onClick = #btnForward")
    public void onForward() {
        Comboitem selectedItem = cbAssignee.getSelectedItem();
        if (selectedItem == null) {
            Messagebox.show("Vui lòng chọn người xử lý tiếp theo!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        User nextUser = selectedItem.getValue();
        try {
            EmailUtil.sendEmail(
                    nextUser.getEmail(),
                    "Văn bản mới được chuyển đến",
                    "Bạn vừa nhận được một văn bản mới: " + selectedDocument.getTitle()
            );
        } catch (MessagingException e) {
            e.printStackTrace();  // Ghi log, không chặn xử lý
        }
        selectedDocument.setAssignedTo(nextUser.getId());
        selectedDocument.setStatus("DANG_XU_LY");

        documentDAO.updateDocument(selectedDocument);
        saveHistory("CHUYEN_TIEP", "Chuyển tiếp: " + txtComment.getValue());
        Executions.sendRedirect("/main.zul");
    }

    @Listen("onClick = #btnReject")
    public void onReject() {
        selectedDocument.setStatus("TU_CHOI");
        selectedDocument.setAssignedTo(null);

        documentDAO.updateDocument(selectedDocument);
        saveHistory("TU_CHOI", "Từ chối: " + txtComment.getValue());
        Executions.sendRedirect("/main.zul");
    }

    @Listen("onClick = #btnBack")
    public void onBack() {
        Executions.sendRedirect("/main.zul");
    }

    private void saveHistory(String action, String comment) {
        DocumentHistory history = new DocumentHistory(
                selectedDocument.getId(), currentUser.getId(), action, comment
        );
        historyDAO.createHistory(history);
    }
}
