package com.example.model;

import java.sql.Timestamp;

public class DocumentHistory {
    private int id;
    private int documentId;
    private int userId;
    private String action;
    private String comment;
    private Timestamp createdAt;
    
    // Additional fields for display
    private String userName;

    // Constructors
    public DocumentHistory() {}

    public DocumentHistory(int documentId, int userId, String action, String comment) {
        this.documentId = documentId;
        this.userId = userId;
        this.action = action;
        this.comment = comment;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getActionDisplay() {
        switch (action) {
            case "TAO_MOI": return "Tạo mới";
            case "CHUYEN_TIEP": return "Chuyển tiếp";
            case "PHE_DUYET": return "Phê duyệt";
            case "TU_CHOI": return "Từ chối";
            default: return action;
        }
    }

    public String getTimeDisplay() {
        if (createdAt != null) {
            return String.format("[%tH:%tM] %s - %s", 
                createdAt, createdAt, userName, getActionDisplay());
        }
        return "";
    }
}
