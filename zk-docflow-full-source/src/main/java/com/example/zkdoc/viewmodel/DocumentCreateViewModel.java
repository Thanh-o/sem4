package com.example.zkdoc.viewmodel;

import com.example.zkdoc.model.Document;
import com.example.zkdoc.model.User;
import com.example.zkdoc.service.DocumentService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

import java.util.Date;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DocumentCreateViewModel {
    private Document document;
    private String type;

    @WireVariable
    private DocumentService documentService;

    @Init
    public void init() {
        document = new Document();
        document.setStatus("PENDING");
        type = "OUTGOING";
        // Kiểm tra user trong session
        User user = (User) Executions.getCurrent().getSession().getAttribute("user");
        if (user == null) {
            Executions.sendRedirect("/zul/login.zul");
        }
    }

    @Command
    public void save() {
        try {
            User user = (User) Executions.getCurrent().getSession().getAttribute("user");
            if (user == null) {
                Messagebox.show("Please log in to create a document", "Error", Messagebox.OK, Messagebox.ERROR);
                Executions.sendRedirect("/zul/login.zul");
                return;
            }
            document.setCreatorId(user.getId());
            document.setType(type);
            if (type.equals("OUTGOING")) {
                document.setIssueDate(new Date());
            } else {
                document.setReceiveDate(new Date());
            }
            documentService.createDocument(document);
            documentService.updateDocumentStatus(document.getId(), "PENDING", user.getId(), "CREATED", "Document created", null);
            Messagebox.show("Document created successfully");
            Executions.sendRedirect("/zul/document_list.zul");
        } catch (Exception e) {
            Messagebox.show("Error: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    // Getters and Setters
    public Document getDocument() { return document; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}