package com.example.model;

import java.sql.Timestamp;

public class Document {
    private int id;
    private String title;
    private String content;
    private String documentType;
    private String status;
    private int createdBy;
    private Integer assignedTo;
    private String address;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String attachment;

    // Additional fields for display
    private String createdByName;
    private String assignedToName;

    // Constructors
    public Document() {}

    public Document(String title, String content, String documentType, int createdBy) {
        this.title = title;
        this.content = content;
        this.documentType = documentType;
        this.createdBy = createdBy;
        this.status = "CHO_XU_LY";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }
    public String getStatusDisplay() {
        switch (status) {
            case "CHO_XU_LY": return "Chờ xử lý";
            case "DANG_XU_LY": return "Đang xử lý";
            case "HOAN_THANH": return "Hoàn thành";
            case "TU_CHOI": return "Từ chối";
            default: return status;
        }
    }

    public String getDocumentTypeDisplay() {
        return "DI".equals(documentType) ? "Văn bản đi" : "Văn bản đến";
    }
}
