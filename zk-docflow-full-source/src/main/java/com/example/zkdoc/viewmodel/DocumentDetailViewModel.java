package com.example.zkdoc.viewmodel;

import com.example.zkdoc.model.Document;
import com.example.zkdoc.model.DocumentHistory;
import com.example.zkdoc.model.User;
import com.example.zkdoc.service.DocumentService;
import com.example.zkdoc.service.UserService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DocumentDetailViewModel {
    private Document document;
    private ListModelList<DocumentHistory> history;
    private ListModelList<User> users;
    private User selectedUser;
    private String comments;

    @WireVariable
    private DocumentService documentService;
    @WireVariable
    private UserService userService;

    @Init
    public void init() {
        document = (Document) Executions.getCurrent().getSession().getAttribute("selectedDocument");
        if (document == null) {
            Executions.sendRedirect("/zul/document_list.zul");
        }
        try {
            history = new ListModelList<>(documentService.getDocumentHistory(document.getId()));
            users = new ListModelList<>(userService.getAllUsers());
            comments = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command
    @NotifyChange({"history", "document"})
    public void approve() {
        try {
            documentService.updateDocumentStatus(document.getId(), "COMPLETED", getCurrentUser().getId(), "APPROVED", comments, null);
            document.setStatus("COMPLETED");
            history = new ListModelList<>(documentService.getDocumentHistory(document.getId()));
            Messagebox.show("Document approved");
        } catch (Exception e) {
            Messagebox.show("Error: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Command
    @NotifyChange({"history", "document"})
    public void forward() {
        if (selectedUser == null) {
            Messagebox.show("Please select a user to forward to", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        try {
            documentService.updateDocumentStatus(document.getId(), "PROCESSING", getCurrentUser().getId(), "FORWARDED", comments, selectedUser.getId());
            document.setStatus("PROCESSING");
            history = new ListModelList<>(documentService.getDocumentHistory(document.getId()));
            Messagebox.show("Document forwarded");
        } catch (Exception e) {
            Messagebox.show("Error: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Command
    @NotifyChange({"history", "document"})
    public void reject() {
        try {
            documentService.updateDocumentStatus(document.getId(), "PENDING", getCurrentUser().getId(), "REJECTED", comments, null);
            document.setStatus("PENDING");
            history = new ListModelList<>(documentService.getDocumentHistory(document.getId()));
            Messagebox.show("Document rejected");
        } catch (Exception e) {
            Messagebox.show("Error: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }
    @Command
    public void goBack() {
        Executions.sendRedirect("/zul/document_list.zul");
    }

    private User getCurrentUser() {
        return (User) Executions.getCurrent().getSession().getAttribute("user");
    }

    // Getters and Setters
    public Document getDocument() { return document; }
    public ListModelList<DocumentHistory> getHistory() { return history; }
    public ListModelList<User> getUsers() { return users; }
    public User getSelectedUser() { return selectedUser; }
    public void setSelectedUser(User selectedUser) { this.selectedUser = selectedUser; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}