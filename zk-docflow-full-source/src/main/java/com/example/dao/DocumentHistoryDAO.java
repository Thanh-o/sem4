package com.example.dao;

import com.example.model.DocumentHistory;
import com.example.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.util.DatabaseUtil.getConnection;

public class DocumentHistoryDAO {
    
    public List<DocumentHistory> getHistoryByDocumentId(int documentId) {
        List<DocumentHistory> histories = new ArrayList<>();
        String sql = "SELECT dh.*, u.full_name as user_name " +
                    "FROM document_history dh " +
                    "JOIN user u ON dh.user_id = u.id " +
                    "WHERE dh.document_id = ? " +
                    "ORDER BY dh.created_at ASC";
        
        try (Connection conn = getConnection();
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

    public void createHistory(DocumentHistory history) {
        String sql = "INSERT INTO document_history (document_id, user_id, action, comment, deadline) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, history.getDocumentId());
            stmt.setInt(2, history.getUserId());
            stmt.setString(3, history.getAction());
            stmt.setString(4, history.getComment());
            stmt.setTimestamp(5, history.getDeadline());  // 👈 thêm dòng này
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        history.setDeadline(rs.getTimestamp("deadline"));
        history.setDocumentTitle(rs.getString("document_title"));

        return history;
    }

    public List<DocumentHistory> getPendingHistoriesByUser(int userId) {
        List<DocumentHistory> list = new ArrayList<>();
        String sql = "SELECT dh.*, d.title as document_title, u.full_name as user_name " +
                "FROM document_history dh " +
                "JOIN document d ON dh.document_id = d.id " +
                "JOIN user u ON dh.user_id = u.id " +
                "WHERE dh.user_id = ? AND dh.deadline IS NOT NULL";


        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DocumentHistory h = mapResultSetToHistory(rs); // hàm bạn đã có
                list.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateDeadline(int historyId, Timestamp newDeadline) {
        String sql = "UPDATE document_history SET deadline = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, newDeadline);
            stmt.setInt(2, historyId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<DocumentHistory> getAllHistoriesWithDeadline() {
        List<DocumentHistory> list = new ArrayList<>();
        String sql = "SELECT dh.*, d.title as document_title, u.full_name as user_name " +
                "FROM document_history dh " +
                "JOIN document d ON dh.document_id = d.id " +
                "JOIN user u ON dh.user_id = u.id " +
                "WHERE dh.deadline IS NOT NULL";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DocumentHistory h = mapResultSetToHistory(rs);
                h.setDocumentTitle(rs.getString("document_title"));
                list.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
