package com.example.viewmodel;

import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

@Getter
@Setter
public class MainLayoutViewModel {

    private String includeSrc = "/dashboard.zul";
    private String userInfo;

    @Init
    public void init() {
        User user = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (user == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }
        userInfo = "Xin chào, " + user.getFullName() + " (" + user.getRole() + ")";
    }

    @Command
    @NotifyChange("includeSrc")
    public void goDashboard() {
        includeSrc = "/dashboard.zul";
    }

    @Command
    @NotifyChange("includeSrc")
    public void goList() {
        includeSrc = "/main.zul";
    }

    @Command
    public void logout() {
        Sessions.getCurrent().invalidate();
        Executions.sendRedirect("/login.zul");
    }
}
