package com.example.viewmodel;

import com.example.dao.UserSessionDAO;
import com.example.model.User;
import com.example.model.UserSession;
import lombok.Getter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Sessions;

import javax.servlet.http.HttpSession;
import java.util.List;

public class SessionManagerViewModel {

    @Getter
    private List<UserSession> sessions;

    @Getter
    private String currentSessionId;

    @Init
    public void init() {
        User user = (User) Sessions.getCurrent().getAttribute("currentUser");
        HttpSession httpSession = (HttpSession) Sessions.getCurrent().getNativeSession();
        currentSessionId = httpSession.getId();
        sessions = UserSessionDAO.getByUserId(user.getId());
    }

    @Command
    @NotifyChange("sessions")
    public void terminateSession(@BindingParam("sessionId") String sessionId) {
        UserSessionDAO.deleteBySessionId(sessionId);
        User user = (User) Sessions.getCurrent().getAttribute("currentUser");
        sessions = UserSessionDAO.getByUserId(user.getId());
    }
}
