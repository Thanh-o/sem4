package com.example.zkdoc.service;

import com.example.zkdoc.model.Document;
import com.example.zkdoc.model.DocumentHistory;
import com.example.zkdoc.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentService {
    public List<Document> getAllDocuments() throws SQLException {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM document";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Document doc = new Document();
                doc.setId(rs.getInt("id"));
                doc.setDocumentNumber(rs.getString("document_number"));
                doc.setTitle(rs.getString("title"));
                doc.setContent(rs.getString("content"));
                doc.setType(rs.getString("type"));
                doc.setRecipientPlace(rs.getString("recipient_place"));
                doc.setSenderPlace(rs.getString("sender_place"));
                doc.setIssueDate(rs.getDate("issue_date"));
                doc.setReceiveDate(rs.getDate("receive_date"));
                doc.setCreatorId(rs.getInt("creator_id"));
                doc.setStatus(rs.getString("status"));
                doc.setAttachmentPath(rs.getString("attachment_path"));
                documents.add(doc);
            }
        }
        return documents;
    }

    public List<Document> searchDocuments(String keyword) throws SQLException {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM document WHERE MATCH(title, content) AGAINST(?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, keyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Document doc = new Document();
                    doc.setId(rs.getInt("id"));
                    doc.setDocumentNumber(rs.getString("document_number"));
                    doc.setTitle(rs.getString("title"));
                    doc.setContent(rs.getString("content"));
                    doc.setType(rs.getString("type"));
                    doc.setRecipientPlace(rs.getString("recipient_place"));
                    doc.setSenderPlace(rs.getString("sender_place"));
                    doc.setIssueDate(rs.getDate("issue_date"));
                    doc.setReceiveDate(rs.getDate("receive_date"));
                    doc.setCreatorId(rs.getInt("creator_id"));
                    doc.setStatus(rs.getString("status"));
                    doc.setAttachmentPath(rs.getString("attachment_path"));
                    documents.add(doc);
                }
            }
        }
        return documents;
    }

    public void createDocument(Document doc) throws SQLException {
        String query = "INSERT INTO document (document_number, title, content, type, recipient_place, sender_place, issue_date, receive_date, creator_id, status, attachment_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, doc.getDocumentNumber());
            stmt.setString(2, doc.getTitle());
            stmt.setString(3, doc.getContent());
            stmt.setString(4, doc.getType());
            stmt.setString(5, doc.getRecipientPlace());
            stmt.setString(6, doc.getSenderPlace());
            stmt.setDate(7, doc.getIssueDate() != null ? new java.sql.Date(doc.getIssueDate().getTime()) : null);
            stmt.setDate(8, doc.getReceiveDate() != null ? new java.sql.Date(doc.getReceiveDate().getTime()) : null);
            stmt.setInt(9, doc.getCreatorId());
            stmt.setString(10, doc.getStatus());
            stmt.setString(11, doc.getAttachmentPath());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    doc.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateDocumentStatus(int documentId, String status, int userId, String action, String comments, Integer nextHandlerId) throws SQLException {
        // Update document status
        String updateQuery = "UPDATE document SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, status);
            stmt.setInt(2, documentId);
            stmt.executeUpdate();
        }

        // Log to document history
        String historyQuery = "INSERT INTO document_history (document_id, user_id, action, comments, next_handler_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(historyQuery)) {
            stmt.setInt(1, documentId);
            stmt.setInt(2, userId);
            stmt.setString(3, action);
            stmt.setString(4, comments);
            stmt.setObject(5, nextHandlerId);
            stmt.executeUpdate();
        }
    }

    public List<DocumentHistory> getDocumentHistory(int documentId) throws SQLException {
        List<DocumentHistory> history = new ArrayList<>();
        String query = "SELECT * FROM document_history WHERE document_id = ? ORDER BY action_date";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, documentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DocumentHistory dh = new DocumentHistory();
                    dh.setId(rs.getInt("id"));
                    dh.setDocumentId(rs.getInt("document_id"));
                    dh.setUserId(rs.getInt("user_id"));
                    dh.setAction(rs.getString("action"));
                    dh.setComments(rs.getString("comments"));
                    dh.setActionDate(rs.getTimestamp("action_date"));
                    dh.setNextHandlerId(rs.getInt("next_handler_id") != 0 ? rs.getInt("next_handler_id") : null);
                    history.add(dh);
                }
            }
        }
        return history;
    }
}