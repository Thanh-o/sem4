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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

public class CreateDocumentComposer extends SelectorComposer<Component> {
    
    @Wire
    private Textbox txtTitle;
    
    @Wire
    private Textbox txtContent;
    
    @Wire
    private Radiogroup rgDocumentType;
    
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
        
        if (title == null || title.trim().isEmpty()) {
            Messagebox.show("Vui lòng nhập tiêu đề!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        
        if (content == null || content.trim().isEmpty()) {
            Messagebox.show("Vui lòng nhập nội dung!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        
        Document document = new Document(title, content, documentType, currentUser.getId());
        
        if (documentDAO.createDocument(document)) {
            // Create history record
            DocumentHistory history = new DocumentHistory(document.getId(), currentUser.getId(), 
                                                         "TAO_MOI", "Tạo văn bản mới");
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
}
