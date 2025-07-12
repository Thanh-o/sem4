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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Wire private Textbox filterTitle;
    @Wire private Textbox filterType;
    @Wire private Textbox filterStatus;
    @Wire private Textbox filterCreatedBy;
    @Wire private Textbox filterAssignedTo;
    @Wire private Button btnViewAll;

    private ListModelList<Document> documentModel;
    private List<Document> allDocumentsCache = new ArrayList<>();

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

        if ("LANHDAO".equals(currentUser.getRole())) {
            btnCreateDocument.setVisible(false);
            btnViewAll.setVisible(true); // hiện nút "Xem tất cả"
        }

        loadDocuments();
        loadStatistics();
    }


    @Listen("onClick = #btnViewAll")
    public void onViewAllDocuments() {
        allDocumentsCache = documentDAO.getAllDocuments();
        documentModel = new ListModelList<>(allDocumentsCache);
        lstDocuments.setModel(documentModel);
    }



    private void loadDocuments() {
        allDocumentsCache = documentDAO.getDocumentsByUser(currentUser.getId(), currentUser.getRole());

        documentModel = new ListModelList<>(allDocumentsCache);
        documentModel.setMultiple(true);
        lstDocuments.setModel(documentModel);
        lstDocuments.setItemRenderer(new ListitemRenderer<Document>() {
            @Override
            public void render(Listitem item, Document document, int index) throws Exception {
                item.setValue(document);

                new Listcell(document.getTitle()).setParent(item);
                new Listcell(document.getDocumentTypeDisplay()).setParent(item);
                new Listcell(document.getStatusDisplay()).setParent(item);
                new Listcell(document.getCreatedByName()).setParent(item);
                new Listcell(document.getAssignedToName()).setParent(item);

                Listcell actionCell = new Listcell();

                Button btnView = new Button("Xem");
                btnView.addEventListener("onClick", event -> viewDocument(document));
                btnView.setParent(actionCell);

                if ("LANHDAO".equals(currentUser.getRole()) &&
                        ("CHO_XU_LY".equals(document.getStatus()) || "DANG_XU_LY".equals(document.getStatus()))) {

                    Button btnProcess = new Button("Xử lý");
                    btnProcess.setTooltiptext("Chuyển sang màn hình xử lý văn bản");
                    btnProcess.setIconSclass("z-icon-edit");
                    btnProcess.addEventListener("onClick", event -> processDocument(document));
                    btnProcess.setParent(actionCell);
                }

                actionCell.setParent(item);
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

    @Listen("onChanging = #filterTitle, #filterType, #filterStatus, #filterCreatedBy, #filterAssignedTo")
    public void onFilterChanging(InputEvent event) {
        filterDocuments();
    }

    @Listen("onChange = #filterTitle, #filterType, #filterStatus, #filterCreatedBy, #filterAssignedTo")
    public void onFilterChange(Event event) {
        filterDocuments();
    }

    private void filterDocuments() {
        String title = filterTitle.getText().toLowerCase();
        String type = filterType.getText().toLowerCase();
        String status = filterStatus.getText().toLowerCase();
        String createdBy = filterCreatedBy.getText().toLowerCase();
        String assignedTo = filterAssignedTo.getText().toLowerCase();

        List<Document> filtered = allDocumentsCache.stream()
                .filter(doc ->
                        (title.isEmpty() || doc.getTitle().toLowerCase().contains(title)) &&
                                (type.isEmpty() || doc.getDocumentTypeDisplay().toLowerCase().contains(type)) &&
                                (status.isEmpty() || doc.getStatusDisplay().toLowerCase().contains(status)) &&
                                (createdBy.isEmpty() || doc.getCreatedByName().toLowerCase().contains(createdBy)) &&
                                (assignedTo.isEmpty() ||
                                        (doc.getAssignedToName() != null &&
                                                doc.getAssignedToName().toLowerCase().contains(assignedTo)))
                )
                .collect(Collectors.toList());

        documentModel = new ListModelList<>(filtered);
        lstDocuments.setModel(documentModel);
    }


}
