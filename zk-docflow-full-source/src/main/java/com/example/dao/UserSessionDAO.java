package com.example.dao;

import com.example.model.UserSession;
import com.example.util.DatabaseUtil;

import java.sql.*;
import java.util.*;

public class UserSessionDAO {

    public static void saveOrUpdate(int userId, String sessionId, String ip, String userAgent) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO user_session(user_id, session_id, ip_address, user_agent, last_accessed) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, sessionId);
            ps.setString(3, ip);
            ps.setString(4, userAgent);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<UserSession> getByUserId(int userId) {
        List<UserSession> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM user_session WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserSession s = new UserSession();
                s.setSessionId(rs.getString("session_id"));
                s.setIpAddress(rs.getString("ip_address"));
                s.setUserAgent(rs.getString("user_agent"));
                s.setLastAccessed(rs.getTimestamp("last_accessed"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void deleteBySessionId(String sessionId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM user_session WHERE session_id = ?");
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLastAccessed(String sessionId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE user_session SET last_accessed = NOW() WHERE session_id = ?");
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
