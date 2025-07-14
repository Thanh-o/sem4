package com.example.dao;

import com.example.model.DocumentAttachment;
import com.example.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentAttachmentDAO {

    public void save(DocumentAttachment att) {
        String sql = "INSERT INTO document_attachment (document_id, filename, original_name, uploaded_by) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, att.getDocumentId());
            stmt.setString(2, att.getFilename());
            stmt.setString(3, att.getOriginalName());
            stmt.setInt(4, att.getUploadedBy());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DocumentAttachment> getByDocumentId(int docId) {
        List<DocumentAttachment> list = new ArrayList<>();
        String sql = "SELECT * FROM document_attachment WHERE document_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, docId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DocumentAttachment att = new DocumentAttachment();
                att.setId(rs.getInt("id"));
                att.setDocumentId(rs.getInt("document_id"));
                att.setFilename(rs.getString("filename"));
                att.setOriginalName(rs.getString("original_name"));
                att.setUploadedBy(rs.getInt("uploaded_by"));
                att.setUploadedAt(rs.getTimestamp("uploaded_at"));
                list.add(att);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM document_attachment WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DocumentAttachment findById(int id) {
        String sql = "SELECT * FROM document_attachment WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DocumentAttachment att = new DocumentAttachment();
                att.setId(rs.getInt("id"));
                att.setDocumentId(rs.getInt("document_id"));
                att.setFilename(rs.getString("filename"));
                att.setOriginalName(rs.getString("original_name"));
                att.setUploadedBy(rs.getInt("uploaded_by"));
                att.setUploadedAt(rs.getTimestamp("uploaded_at"));
                return att;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
