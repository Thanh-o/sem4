package com.example.viewmodel;

import com.example.dao.WorkflowDAO;
import com.example.model.WorkflowStep;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

import java.util.ArrayList;
import java.util.List;

public class WorkflowConfigViewModel {

    @Getter @Setter
    private String stepName;

    @Getter @Setter
    private String roleCode;

    @Getter
    private List<WorkflowStep> stepList = new ArrayList<>();

    private WorkflowDAO workflowDAO = new WorkflowDAO();

    @Init
    public void init() {
        stepList = workflowDAO.getWorkflow();
        drawCanvas();
    }

    @Command
    @NotifyChange({"stepList", "stepName", "roleCode"})
    public void addStep() {
        if (stepName == null || stepName.trim().isEmpty() ||
                roleCode == null || roleCode.trim().isEmpty()) {
            Clients.showNotification("Vui lòng nhập tên bước và mã vai trò.", "warning", null, "top_center", 2000);
            return;
        }

        stepList.add(new WorkflowStep(stepList.size() + 1, roleCode.toUpperCase().trim(), stepName.trim()));
        stepName = "";
        roleCode = "";
        drawCanvas();
    }

    @Command
    @NotifyChange("stepList")
    public void deleteStep(@BindingParam("step") WorkflowStep step) {
        stepList.remove(step);
        // Cập nhật lại số thứ tự
        for (int i = 0; i < stepList.size(); i++) {
            stepList.get(i).setStepOrder(i + 1);
        }
        drawCanvas();
    }

    @Command
    public void saveWorkflow() {
        workflowDAO.saveWorkflow(stepList);
        Clients.showNotification("Đã lưu quy trình thành công!", "info", null, "top_center", 2000);
    }

    private void drawCanvas() {
        Clients.evalJavaScript("clearCanvas();");
        int x = 20;
        int y = 60;

        StringBuilder js = new StringBuilder();
        for (int i = 0; i < stepList.size(); i++) {
            WorkflowStep step = stepList.get(i);
            String label = step.getStepName() + " (" + step.getRoleCode() + ")";
            js.append("drawBox(").append(x).append(", ").append(y).append(", '").append(label).append("');\n");

            if (i < stepList.size() - 1) {
                js.append("drawArrow(")
                        .append(x + 140).append(", ").append(y + 25).append(", ")
                        .append(x + 160).append(", ").append(y + 25).append(");\n");
            }

            x += 160;
        }

        Clients.evalJavaScript(js.toString());
    }
    @Command
    public void goBack() {
        Executions.sendRedirect("/main_layout.zul");
    }
}
