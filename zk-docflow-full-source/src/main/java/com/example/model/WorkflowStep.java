package com.example.model;

public class WorkflowStep {
    private int id;
    private int stepOrder;
    private String roleCode;
    private String stepName;

    // Constructors
    public WorkflowStep() {}
    public WorkflowStep(int stepOrder, String roleCode, String stepName) {
        this.stepOrder = stepOrder;
        this.roleCode = roleCode;
        this.stepName = stepName;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
}
