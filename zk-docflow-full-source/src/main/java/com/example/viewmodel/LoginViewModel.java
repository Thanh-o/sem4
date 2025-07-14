package com.example.viewmodel;

import com.example.dao.UserDAO;
import com.example.dao.UserSessionDAO;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import javax.servlet.http.HttpSession;

public class LoginViewModel {

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    private UserDAO userDAO = new UserDAO();

    @Command
    @NotifyChange({"username", "password"})
    public void login() {
        if (username == null || username.trim().isEmpty()) {
            Messagebox.show("Vui lòng nhập tên đăng nhập!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            Messagebox.show("Vui lòng nhập mật khẩu!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            Sessions.getCurrent().setAttribute("currentUser", user);

            HttpSession httpSession = (HttpSession) Sessions.getCurrent().getNativeSession();
            String sessionId = httpSession.getId();
            String ip = Executions.getCurrent().getRemoteAddr();
            String userAgent = Executions.getCurrent().getHeader("user-agent");

            UserSessionDAO.saveOrUpdate(user.getId(), sessionId, ip, userAgent);

            Executions.sendRedirect("/main_layout.zul");
        } else {
            Messagebox.show("Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
