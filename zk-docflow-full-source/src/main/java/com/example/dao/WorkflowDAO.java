package com.example.dao;

import com.example.model.WorkflowStep;
import com.example.util.DatabaseUtil;

import java.sql.*;
import java.util.*;

public class WorkflowDAO {

    public List<WorkflowStep> getWorkflow() {
        List<WorkflowStep> steps = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM workflow_step ORDER BY step_order";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                WorkflowStep step = new WorkflowStep();
                step.setId(rs.getInt("id"));
                step.setStepOrder(rs.getInt("step_order"));
                step.setRoleCode(rs.getString("role_code"));
                step.setStepName(rs.getString("step_name"));
                step.setDurationDays(rs.getInt("duration_days"));

                steps.add(step);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }

    public void saveWorkflow(List<WorkflowStep> steps) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM workflow_step");
            deleteStmt.executeUpdate();

            String insertSql = "INSERT INTO workflow_step (step_order, role_code, step_name, duration_days) VALUES (?, ?, ?, ?)";

            PreparedStatement insertStmt = conn.prepareStatement(insertSql);

            for (int i = 0; i < steps.size(); i++) {
                WorkflowStep s = steps.get(i);
                insertStmt.setInt(1, i + 1);
                insertStmt.setString(2, s.getRoleCode());
                insertStmt.setString(3, s.getStepName());
                insertStmt.setInt(4, s.getDurationDays() != null ? s.getDurationDays() : 2);

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WorkflowStep findStepByOrder(int order) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM workflow_step WHERE step_order = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, order);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                WorkflowStep step = new WorkflowStep();
                step.setId(rs.getInt("id"));
                step.setStepOrder(rs.getInt("step_order"));
                step.setRoleCode(rs.getString("role_code"));
                step.setStepName(rs.getString("step_name"));
                step.setDurationDays(rs.getInt("duration_days"));

                return step;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int findStepIndexByRole(String roleCode) {
        List<WorkflowStep> steps = getWorkflow();
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).getRoleCode().equals(roleCode)) {
                return i;
            }
        }
        return -1;
    }
    public WorkflowStep getStepByRole(String roleCode) {
        return getWorkflow().stream()
                .filter(step -> step.getRoleCode().equalsIgnoreCase(roleCode))
                .findFirst()
                .orElse(null);
    }

}
