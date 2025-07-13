package com.example.job;

import com.example.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeadlineChecker {

    public static void checkAndMarkOverdue() {
        String sql = "UPDATE document_history " +
                "SET is_overdue = TRUE " +
                "WHERE deadline IS NOT NULL " +
                "AND deadline < NOW() " +
                "AND is_overdue = FALSE";


        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int count = stmt.executeUpdate();
            System.out.println("📌 [DeadlineChecker] Đánh dấu quá hạn: " + count + " bản ghi.");
        } catch (SQLException e) {
            System.err.println("❌ [DeadlineChecker] Lỗi khi cập nhật quá hạn:");
            e.printStackTrace();
        }
    }
}
