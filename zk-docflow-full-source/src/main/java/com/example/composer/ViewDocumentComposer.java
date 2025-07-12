package com.example.composer;

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
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import java.text.SimpleDateFormat;
import java.util.List;

public class ViewDocumentComposer extends SelectorComposer<Component> {
    
    @Wire
    private Label lblTitle;
    
    @Wire
    private Label lblDocumentType;
    
    @Wire
    private Label lblStatus;
    
    @Wire
    private Label lblCreatedBy;
    
    @Wire
    private Label lblAssignedTo;
    
    @Wire
    private Label lblCreatedAt;
    
    @Wire
    private Textbox txtContent;
    
    @Wire
    private Vbox vboxTimeline;
    
    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private Document selectedDocument;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
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
        lblTitle.setValue(selectedDocument.getTitle());
        lblDocumentType.setValue(selectedDocument.getDocumentTypeDisplay());
        lblStatus.setValue(selectedDocument.getStatusDisplay());
        lblCreatedBy.setValue(selectedDocument.getCreatedByName());
        lblAssignedTo.setValue(selectedDocument.getAssignedToName() != null ? 
                              selectedDocument.getAssignedToName() : "Chưa giao");
        lblCreatedAt.setValue(dateFormat.format(selectedDocument.getCreatedAt()));
        txtContent.setValue(selectedDocument.getContent());
    }
    
    private void loadTimeline() {
        List<DocumentHistory> histories = historyDAO.getHistoryByDocumentId(selectedDocument.getId());
        
        vboxTimeline.getChildren().clear();
        
        for (DocumentHistory history : histories) {
            Label timelineItem = new Label();
            timelineItem.setValue(history.getTimeDisplay() + 
                                 (history.getComment() != null && !history.getComment().isEmpty() ? 
                                  " - " + history.getComment() : ""));
            timelineItem.setStyle("padding: 5px; border-left: 3px solid #007bff; margin-left: 10px; background-color: #f8f9fa;");
            timelineItem.setParent(vboxTimeline);
        }
    }
    
    @Listen("onClick = #btnBack")
    public void goBack() {
        Executions.sendRedirect("/main.zul");
    }
}
