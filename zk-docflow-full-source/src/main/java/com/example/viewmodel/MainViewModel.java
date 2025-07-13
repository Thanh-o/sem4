package com.example.viewmodel;

import com.example.dao.DocumentDAO;
import com.example.model.Document;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel {

    @Getter @Setter
    private String filterTitle = "";
    @Getter @Setter
    private String filterType = "";
    @Getter @Setter
    private String filterStatus = "";
    @Getter @Setter
    private String filterCreatedBy = "";
    @Getter @Setter
    private String filterAssignedTo = "";

    @Getter
    private ListModelList<Document> documents = new ListModelList<>();
    @Getter
    private boolean showWorkflowButton;

    private List<Document> allDocumentsCache = new ArrayList<>();
    private DocumentDAO documentDAO = new DocumentDAO();
    private User currentUser;

    @Init
    public void init() {
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }

        if ("ADMIN".equals(currentUser.getRole())) {
            showWorkflowButton = true;
            allDocumentsCache = documentDAO.getAllDocuments();
        } else {
            allDocumentsCache = documentDAO.getDocumentsByUser(currentUser.getId(), currentUser.getRole());
        }

        documents = new ListModelList<>(allDocumentsCache);
    }

    @Command
    @NotifyChange("documents")
    public void filterDocuments() {
        List<Document> filtered = allDocumentsCache.stream()
                .filter(doc ->
                        (filterTitle.isEmpty() || doc.getTitle().toLowerCase().contains(filterTitle.toLowerCase())) &&
                                (filterType.isEmpty() || doc.getDocumentTypeDisplay().toLowerCase().contains(filterType.toLowerCase())) &&
                                (filterStatus.isEmpty() || doc.getStatusDisplay().toLowerCase().contains(filterStatus.toLowerCase())) &&
                                (filterCreatedBy.isEmpty() || doc.getCreatedByName().toLowerCase().contains(filterCreatedBy.toLowerCase())) &&
                                (filterAssignedTo.isEmpty() ||
                                        (doc.getAssignedToName() != null &&
                                                doc.getAssignedToName().toLowerCase().contains(filterAssignedTo.toLowerCase())))
                )
                .collect(Collectors.toList());
        documents = new ListModelList<>(filtered);
    }

    @Command
    public void createDocument() {
        Executions.sendRedirect("/create-document.zul");
    }

    @Command
    public void openWorkflowConfig() {
        Executions.sendRedirect("/workflow-config.zul");
    }

    @Command
    public void logout() {
        Sessions.getCurrent().invalidate();
        Executions.sendRedirect("/login.zul");
    }

    @Command
    public void viewDocument(@BindingParam("doc") Document doc) {
        Sessions.getCurrent().setAttribute("selectedDocument", doc);
        Executions.sendRedirect("/view-document.zul");
    }

    @Command
    public void processDocument(@BindingParam("doc") Document doc) {
        Sessions.getCurrent().setAttribute("selectedDocument", doc);
        Executions.sendRedirect("/process-document.zul");
    }

    public boolean canProcess(Document doc) {
        return ("ADMIN".equals(currentUser.getRole()) ||
                (doc.getAssignedTo() != null && doc.getAssignedTo().equals(currentUser.getId()))) &&
                "DANG_XU_LY".equals(doc.getStatus());
    }


}
