package com.example.composer;

import com.example.dao.UserDAO;
import com.example.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class LoginComposer extends SelectorComposer<Component> {
    
    @Wire
    private Textbox txtUsername;
    
    @Wire
    private Textbox txtPassword;
    
    private UserDAO userDAO = new UserDAO();
    
    @Listen("onClick = #btnLogin")
    public void doLogin() {
        String username = txtUsername.getValue();
        String password = txtPassword.getValue();
        
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
            Executions.sendRedirect("/main.zul");
        } else {
            Messagebox.show("Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
