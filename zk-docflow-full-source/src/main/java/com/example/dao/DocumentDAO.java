package com.example.dao;

import com.example.model.Document;
import com.example.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentDAO {
    
    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT d.*, u1.full_name as created_by_name, u2.full_name as assigned_to_name " +
                    "FROM document d " +
                    "LEFT JOIN user u1 ON d.created_by = u1.id " +
                    "LEFT JOIN user u2 ON d.assigned_to = u2.id " +
                    "ORDER BY d.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    public List<Document> getDocumentsByUser(int userId, String role) {
        List<Document> documents = new ArrayList<>();
        String sql;
        
        if ("LANHDAO".equals(role)) {
            // Leaders can see all documents assigned to them or pending
            sql = "SELECT d.*, u1.full_name as created_by_name, u2.full_name as assigned_to_name " +
                  "FROM document d " +
                  "LEFT JOIN user u1 ON d.created_by = u1.id " +
                  "LEFT JOIN user u2 ON d.assigned_to = u2.id " +
                  "WHERE d.assigned_to = ? OR d.status = 'CHO_XU_LY' " +
                  "ORDER BY d.created_at DESC";
        } else {
            // Employees can only see documents they created
            sql = "SELECT d.*, u1.full_name as created_by_name, u2.full_name as assigned_to_name " +
                  "FROM document d " +
                  "LEFT JOIN user u1 ON d.created_by = u1.id " +
                  "LEFT JOIN user u2 ON d.assigned_to = u2.id " +
                  "WHERE d.created_by = ? " +
                  "ORDER BY d.created_at DESC";
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    public Document getDocumentById(int id) {
        String sql = "SELECT d.*, u1.full_name as created_by_name, u2.full_name as assigned_to_name " +
                    "FROM document d " +
                    "LEFT JOIN user u1 ON d.created_by = u1.id " +
                    "LEFT JOIN user u2 ON d.assigned_to = u2.id " +
                    "WHERE d.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDocument(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createDocument(Document document) {
        String sql = "INSERT INTO document (title, content, document_type, created_by, status, address, attachment) VALUES (?, ?, ?, ?, ?, ?, ?)";



        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getContent());
            stmt.setString(3, document.getDocumentType());
            stmt.setInt(4, document.getCreatedBy());
            stmt.setString(5, document.getStatus());
            stmt.setString(6, document.getAddress());
            stmt.setString(7, document.getAttachment());

            int result = stmt.executeUpdate();
            
            if (result > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    document.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDocument(Document document) {
        String sql = "UPDATE document SET title = ?, content = ?, status = ?, assigned_to = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getContent());
            stmt.setString(3, document.getStatus());
            if (document.getAssignedTo() != null) {
                stmt.setInt(4, document.getAssignedTo());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, document.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Integer> getDocumentStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        // Count by type
        String typeSql = "SELECT document_type, COUNT(*) as count FROM document GROUP BY document_type";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(typeSql)) {
            
            while (rs.next()) {
                String type = "DI".equals(rs.getString("document_type")) ? "Văn bản đi" : "Văn bản đến";
                stats.put(type, rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Count by status
        String statusSql = "SELECT status, COUNT(*) as count FROM document GROUP BY status";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(statusSql)) {
            
            while (rs.next()) {
                String status = rs.getString("status");
                String statusDisplay;
                switch (status) {
                    case "CHO_XU_LY": statusDisplay = "Chờ xử lý"; break;
                    case "DANG_XU_LY": statusDisplay = "Đang xử lý"; break;
                    case "HOAN_THANH": statusDisplay = "Hoàn thành"; break;
                    case "TU_CHOI": statusDisplay = "Từ chối"; break;
                    default: statusDisplay = status;
                }
                stats.put(statusDisplay, rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document document = new Document();
        document.setId(rs.getInt("id"));
        document.setTitle(rs.getString("title"));
        document.setContent(rs.getString("content"));
        document.setDocumentType(rs.getString("document_type"));
        document.setStatus(rs.getString("status"));
        document.setCreatedBy(rs.getInt("created_by"));
        document.setAddress(rs.getString("address"));
        document.setAttachment(rs.getString("attachment"));


        int assignedTo = rs.getInt("assigned_to");
        if (!rs.wasNull()) {
            document.setAssignedTo(assignedTo);
        }
        
        document.setCreatedAt(rs.getTimestamp("created_at"));
        document.setUpdatedAt(rs.getTimestamp("updated_at"));
        document.setCreatedByName(rs.getString("created_by_name"));
        document.setAssignedToName(rs.getString("assigned_to_name"));
        
        return document;
    }
}
