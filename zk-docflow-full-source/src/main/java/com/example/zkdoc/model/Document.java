package com.example.zkdoc.model;

import java.util.Date;

public class Document {
    private int id;
    private String documentNumber;
    private String title;
    private String content;
    private String type; // OUTGOING, INCOMING
    private String recipientPlace;
    private String senderPlace;
    private Date issueDate;
    private Date receiveDate;
    private int creatorId;
    private String status; // PENDING, PROCESSING, COMPLETED
    private String attachmentPath;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRecipientPlace() { return recipientPlace; }
    public void setRecipientPlace(String recipientPlace) { this.recipientPlace = recipientPlace; }
    public String getSenderPlace() { return senderPlace; }
    public void setSenderPlace(String senderPlace) { this.senderPlace = senderPlace; }
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public Date getReceiveDate() { return receiveDate; }
    public void setReceiveDate(Date receiveDate) { this.receiveDate = receiveDate; }
    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
}