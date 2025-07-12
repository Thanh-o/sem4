package com.example.zkdoc.viewmodel;

import com.example.zkdoc.model.Document;
import com.example.zkdoc.service.DocumentService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DocumentListViewModel {
    private ListModelList<Document> documents;
    private String searchKeyword;

    @WireVariable
    private DocumentService documentService;

    @Init
    public void init() {
        try {
            documents = new ListModelList<>(documentService.getAllDocuments());
            searchKeyword = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command
    @NotifyChange("documents")
    public void search() {
        try {
            if (searchKeyword.isEmpty()) {
                documents = new ListModelList<>(documentService.getAllDocuments());
            } else {
                documents = new ListModelList<>(documentService.searchDocuments(searchKeyword));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command
    public void viewDetails(Document document) {
        Executions.getCurrent().getSession().setAttribute("selectedDocument", document);
        Executions.sendRedirect("/zul/document_detail.zul");
    }

    @Command
    public void createNew() {
        Executions.sendRedirect("/zul/document_create.zul");
    }

    // Getters and Setters
    public ListModelList<Document> getDocuments() { return documents; }
    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
}