package com.example.zkdoc.model;

import java.util.Date;

public class DocumentHistory {
    private int id;
    private int documentId;
    private int userId;
    private String action; // CREATED, APPROVED, FORWARDED, REJECTED
    private String comments;
    private Date actionDate;
    private Integer nextHandlerId;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public Date getActionDate() { return actionDate; }
    public void setActionDate(Date actionDate) { this.actionDate = actionDate; }
    public Integer getNextHandlerId() { return nextHandlerId; }
    public void setNextHandlerId(Integer nextHandlerId) { this.nextHandlerId = nextHandlerId; }
}