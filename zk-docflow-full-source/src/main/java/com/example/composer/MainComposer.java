package com.example.composer;

import com.example.dao.DocumentDAO;
import com.example.dao.DocumentHistoryDAO;
import com.example.dao.UserDAO;
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

import java.util.List;
import java.util.Map;

public class MainComposer extends SelectorComposer<Component> {
    
    @Wire
    private Label lblWelcome;
    
    @Wire
    private Listbox lstDocuments;
    
    @Wire
    private Grid gridStats;
    
    @Wire
    private Button btnCreateDocument;
    
    @Wire
    private Button btnLogout;
    
    private DocumentDAO documentDAO = new DocumentDAO();
    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();
    private UserDAO userDAO = new UserDAO();
    private User currentUser;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }
        
        lblWelcome.setValue("Xin chào, " + currentUser.getFullName() + " (" + 
                           ("LANHDAO".equals(currentUser.getRole()) ? "Lãnh đạo" : "Nhân viên") + ")");
        
        // Hide create button for leaders if needed
        if ("LANHDAO".equals(currentUser.getRole())) {
            btnCreateDocument.setVisible(false);
        }
        
        loadDocuments();
        loadStatistics();
    }
    
    private void loadDocuments() {
        List<Document> documents = documentDAO.getDocumentsByUser(currentUser.getId(), currentUser.getRole());
        
        ListModelList<Document> model = new ListModelList<>(documents);
        lstDocuments.setModel(model);
        lstDocuments.setItemRenderer(new ListitemRenderer<Document>() {
            @Override
            public void render(Listitem item, Document document, int index) throws Exception {
                item.setValue(document);
                
                new Listcell(document.getTitle()).setParent(item);
                new Listcell(document.getDocumentTypeDisplay()).setParent(item);
                new Listcell(document.getStatusDisplay()).setParent(item);
                new Listcell(document.getCreatedByName()).setParent(item);
                new Listcell(document.getAssignedToName()).setParent(item);
                
                // Action buttons
                Listcell actionCell = new Listcell();
                actionCell.setParent(item);
                
                Button btnView = new Button("Xem");
                btnView.addEventListener("onClick", event -> viewDocument(document));
                btnView.setParent(actionCell);
                
                if ("LANHDAO".equals(currentUser.getRole()) && 
                    ("CHO_XU_LY".equals(document.getStatus()) || "DANG_XU_LY".equals(document.getStatus()))) {
                    Button btnProcess = new Button("Xử lý");
                    btnProcess.addEventListener("onClick", event -> processDocument(document));
                    btnProcess.setParent(actionCell);
                }
            }
        });
    }
    
    private void loadStatistics() {
        Map<String, Integer> stats = documentDAO.getDocumentStatistics();
        
        Rows rows = gridStats.getRows();
        rows.getChildren().clear();
        
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            Row row = new Row();
            row.setParent(rows);
            
            new Label(entry.getKey()).setParent(row);
            new Label(String.valueOf(entry.getValue())).setParent(row);
        }
    }
    
    @Listen("onClick = #btnCreateDocument")
    public void createDocument() {
        Executions.sendRedirect("/create-document.zul");
    }
    
    @Listen("onClick = #btnLogout")
    public void logout() {
        Sessions.getCurrent().invalidate();
        Executions.sendRedirect("/login.zul");
    }
    
    private void viewDocument(Document document) {
        Sessions.getCurrent().setAttribute("selectedDocument", document);
        Executions.sendRedirect("/view-document.zul");
    }
    
    private void processDocument(Document document) {
        Sessions.getCurrent().setAttribute("selectedDocument", document);
        Executions.sendRedirect("/process-document.zul");
    }
}
