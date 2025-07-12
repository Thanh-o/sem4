package com.example.zkdoc.viewmodel;

import com.example.zkdoc.model.User;
import com.example.zkdoc.service.UserService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginViewModel {
    private String username;
    private String password;

    @WireVariable
    private UserService userService;

    @Init
    public void init() {
        username = "";
        password = "";
    }

    @Command
    public void login() {
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                Executions.getCurrent().getSession().setAttribute("user", user);
                Executions.sendRedirect("/zul/document_list.zul");
            } else {
                Messagebox.show("Invalid username or password", "Error", Messagebox.OK, Messagebox.ERROR);
            }
        } catch (Exception e) {
            Messagebox.show("Error during login: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}