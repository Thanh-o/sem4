package com.example.viewmodel;

import com.example.dao.DocumentHistoryDAO;
import com.example.model.DocumentHistory;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.sql.Timestamp;
import java.util.List;

public class PersonalCalendarViewModel {

    private DocumentHistoryDAO historyDAO = new DocumentHistoryDAO();

    @Getter @Setter
    private List<DocumentHistory> documentList;

    private User currentUser;

    @Init
    public void init() {
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            // Admin xem toàn bộ
            documentList = historyDAO.getAllHistoriesWithDeadline();
        } else {
            // Người thường chỉ xem của chính họ
            documentList = historyDAO.getPendingHistoriesByUser(currentUser.getId());
        }
    }

    @Command
    @NotifyChange("documentList")
    public void updateDeadline(@BindingParam("history") DocumentHistory history) {
        if (history.getDeadline() == null) {
            Messagebox.show("Vui lòng chọn ngày hạn xử lý.");
            return;
        }

        historyDAO.updateDeadline(history.getId(), history.getDeadline());
        Messagebox.show("Cập nhật hạn xử lý thành công.");
    }

    @Command
    public void goBack() {
        Executions.sendRedirect("/main_layout.zul");
    }
}
