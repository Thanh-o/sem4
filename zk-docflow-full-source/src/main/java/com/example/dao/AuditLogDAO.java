package com.example.dao;

import com.example.model.AuditLog;
import com.example.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public void log(int userId, String actionType, String description) {
        String sql = "INSERT INTO audit_log (user_id, action_type, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, actionType);
            stmt.setString(3, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<AuditLog> getAllLogs() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name FROM audit_log a JOIN user u ON a.user_id = u.id ORDER BY a.action_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUserName(rs.getString("full_name"));
                log.setActionType(rs.getString("action_type"));
                log.setDescription(rs.getString("description"));
                log.setActionTime(rs.getTimestamp("action_time"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
