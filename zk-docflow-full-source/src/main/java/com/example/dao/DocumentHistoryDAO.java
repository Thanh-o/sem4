package com.example.dao;

import com.example.model.DocumentHistory;
import com.example.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentHistoryDAO {
    
    public List<DocumentHistory> getHistoryByDocumentId(int documentId) {
        List<DocumentHistory> histories = new ArrayList<>();
        String sql = "SELECT dh.*, u.full_name as user_name " +
                    "FROM document_history dh " +
                    "JOIN user u ON dh.user_id = u.id " +
                    "WHERE dh.document_id = ? " +
                    "ORDER BY dh.created_at ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, documentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                histories.add(mapResultSetToHistory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }

    public boolean createHistory(DocumentHistory history) {
        String sql = "INSERT INTO document_history (document_id, user_id, action, comment) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, history.getDocumentId());
            stmt.setInt(2, history.getUserId());
            stmt.setString(3, history.getAction());
            stmt.setString(4, history.getComment());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private DocumentHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        DocumentHistory history = new DocumentHistory();
        history.setId(rs.getInt("id"));
        history.setDocumentId(rs.getInt("document_id"));
        history.setUserId(rs.getInt("user_id"));
        history.setAction(rs.getString("action"));
        history.setComment(rs.getString("comment"));
        history.setCreatedAt(rs.getTimestamp("created_at"));
        history.setUserName(rs.getString("user_name"));
        return history;
    }
}
