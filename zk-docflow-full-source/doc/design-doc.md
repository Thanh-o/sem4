package com.example.zkdoc.model;

import java.util.Date;

public class Document {
private int id;
private String type;
private String title;
private String content;
private String code;
private Date date;
private int createdBy;
private String receiverOrSender;
private String status;

    public Document() {}
    public Document(int id, String type, String title, String content, String code,
                    Date date, int createdBy, String receiverOrSender, String status) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.code = code;
        this.date = date;
        this.createdBy = createdBy;
        this.receiverOrSender = receiverOrSender;
        this.status = status;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public int getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getReceiverOrSender() {
        return receiverOrSender;
    }
    public void setReceiverOrSender(String receiverOrSender) {
        this.receiverOrSender = receiverOrSender;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
package com.example.zkdoc.model;

import java.util.Date;

public class DocumentHistory {
private int id;
private int documentId;
private int handlerId;
private String action;
private Date time;
private String note;

    public DocumentHistory() {}
    public DocumentHistory(int id, int documentId, int handlerId, String action, Date time, String note) {
        this.id = id;
        this.documentId = documentId;
        this.handlerId = handlerId;
        this.action = action;
        this.time = time;
        this.note = note;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getDocumentId() {
        return documentId;
    }
    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getHandlerId() {
        return handlerId;
    }
    public void setHandlerId(int handlerId) {
        this.handlerId = handlerId;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

}
package com.example.zkdoc.model;

public class User {
private int id;
private String username;
private String password;
private String role; // NHANVIEN, LANHDAO

    public User() {}
    public User(int id, String username, String password, String role) {
        this.id = id; this.username = username; this.password = password; this.role = role;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

}
package com.example.zkdoc.service;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
public static Connection getConnection() throws Exception {
String url = "jdbc:mysql://localhost:3306/zk_docflow?useSSL=false";
String user = "root";
String pass = "123456";
Class.forName("com.mysql.cj.jdbc.Driver");
return DriverManager.getConnection(url, user, pass);
}
}
package com.example.zkdoc.viewmodel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

public class DocumentCreateViewModel {
private String type = "DI";
private String title;
private String content;
private String code;
private String receiverOrSender;

    @Command
    public void save() {
        // TODO: Lưu dữ liệu vào DB hoặc service
        Clients.showNotification("Đã lưu văn bản mới", "info", null, "top_center", 3000);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReceiverOrSender() {
        return receiverOrSender;
    }

    public void setReceiverOrSender(String receiverOrSender) {
        this.receiverOrSender = receiverOrSender;
    }
}
package com.example.zkdoc.viewmodel;

import com.example.zkdoc.model.Document;
import org.zkoss.bind.annotation.*;
import org.zkoss.zul.ListModelList;

import java.util.Date;

public class DocumentListViewModel {
private ListModelList<Document> documents;
private String keyword = "";

    @Init
    public void init() {
        documents = new ListModelList<>();
        loadDocuments();
    }

    private void loadDocuments() {
        documents.clear();
        documents.add(new Document(1, "DI", "Thư mời họp", "Nội dung họp tổng kết", "CV001", new Date(), 1, "Sở Nội vụ", "CHO_XU_LY"));
        documents.add(new Document(2, "DEN", "Báo cáo tài chính", "Nội dung báo cáo quý", "CV002", new Date(), 2, "UBND Tỉnh", "DANG_XU_LY"));
    }

    @Command
    @NotifyChange("documents")
    public void search() {
        loadDocuments();
        if (!keyword.isEmpty()) {
            documents.removeIf(doc -> !doc.getTitle().toLowerCase().contains(keyword.toLowerCase()));
        }
    }

    public ListModelList<Document> getDocuments() {
        return documents;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
package com.example.zkdoc.viewmodel;

import com.example.zkdoc.model.Document;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

public class DocumentProcessViewModel {
private Document selectedDocument;

    @Init
    public void init(@ExecutionArgParam("document") Document document) {
        this.selectedDocument = document;
    }

    @Command
    public void approve() {
        selectedDocument.setStatus("HOAN_THANH");
        Clients.showNotification("Văn bản đã được phê duyệt", "info", null, "top_center", 2000);
    }

    @Command
    public void forward() {
        Clients.showNotification("Văn bản đã được chuyển tiếp", "info", null, "top_center", 2000);
    }

    @Command
    public void reject() {
        selectedDocument.setStatus("TU_CHOI");
        Clients.showNotification("Văn bản đã bị từ chối", "warning", null, "top_center", 2000);
    }

    public Document getSelectedDocument() {
        return selectedDocument;
    }
}
package com.example.zkdoc.viewmodel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

public class LoginViewModel {
private String username;
private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Command
    @NotifyChange({"username", "password"})
    public void login() {
        if (("nhanvien1".equals(username) || "lanhdao1".equals(username)) && "123456".equals(password)) {
            Clients.showNotification("Đăng nhập thành công", "info", null, "top_center", 2000);
            Executions.getCurrent().getSession().setAttribute("currentUser", username);
            Executions.sendRedirect("/zul/dashboard.zul");
        } else {
            Clients.showNotification("Sai tài khoản hoặc mật khẩu!", "error", null, "top_center", 3000);
        }
    }
}

// dashboard.zul
<window title="Thống kê văn bản" border="normal" width="100%">
<vlayout style="padding: 15px">
<label value="Tổng hợp số lượng văn bản" style="font-weight: bold; font-size: 16px"/>
<grid>
<rows>
<row>
<label value="Văn bản Đi:"/>
<label value="5"/>
</row>
<row>
<label value="Văn bản Đến:"/>
<label value="3"/>
</row>
<row>
<label value="Đang xử lý:"/>
<label value="4"/>
</row>
<row>
<label value="Hoàn thành:"/>
<label value="2"/>
</row>
</rows>
</grid>
</vlayout>
</window>

// document_create.zul
<window title="Tạo văn bản mới" border="normal" width="600px"
viewModel="@id('vm') @init('com.example.zkdoc.viewmodel.DocumentCreateViewModel')">
<grid width="100%">
<rows>
<row>
<label value="Loại văn bản"/>
<combobox value="@bind(vm.type)">
<comboitem label="Đi" value="DI"/>
<comboitem label="Đến" value="DEN"/>
</combobox>
</row>
<row>
<label value="Tiêu đề"/>
<textbox value="@bind(vm.title)" width="400px"/>
</row>
<row>
<label value="Nội dung"/>
<textbox value="@bind(vm.content)" rows="4" multiline="true" width="400px"/>
</row>
<row>
<label value="Số hiệu"/>
<textbox value="@bind(vm.code)" width="200px"/>
</row>
<row>
<label value="@load(vm.type == 'DI' ? 'Nơi nhận' : 'Nơi gửi')"/>
<textbox value="@bind(vm.receiverOrSender)" width="300px"/>
</row>
<row>
<label value="Tệp đính kèm"/>
<fileupload label="Chọn tệp..."/>
</row>
<row>
<button label="Lưu văn bản" onClick="@command('save')" width="150px"/>
</row>
</rows>
</grid>
</window>

//document_list.zul
<window title="Danh sách văn bản" border="normal" width="100%"
viewModel="@id('vm') @init('com.example.zkdoc.viewmodel.DocumentListViewModel')">
<vlayout spacing="10px" style="padding:15px">
<hbox>
<textbox placeholder="Tìm tiêu đề..." value="@bind(vm.keyword)" width="300px"/>
<button label="Tìm" onClick="@command('search')"/>
</hbox>

        <listbox model="@bind(vm.documents)" width="100%" mold="paging" pageSize="5">
            <listhead>
                <listheader label="Loại" width="80px"/>
                <listheader label="Số hiệu" width="100px"/>
                <listheader label="Tiêu đề"/>
                <listheader label="Ngày" width="120px"/>
                <listheader label="Trạng thái" width="150px"/>
            </listhead>
            <template name="model">
                <listitem>
                    <listcell label="@load(each.type)"/>
                    <listcell label="@load(each.code)"/>
                    <listcell label="@load(each.title)"/>
                    <listcell label="@load(each.date)"/>
                    <listcell label="@load(each.status)"/>
                </listitem>
            </template>
        </listbox>
    </vlayout>
</window>

//document_process.zul
<window title="Xử lý văn bản" border="normal" width="600px"
viewModel="@id('vm') @init('com.example.zkdoc.viewmodel.DocumentProcessViewModel')"
apply="org.zkoss.bind.BindComposer">
<vlayout style="padding:15px">
<label value="Tiêu đề: @load(vm.selectedDocument.title)" style="font-weight:bold"/>
<label value="Loại: @load(vm.selectedDocument.type)"/>
<label value="Số hiệu: @load(vm.selectedDocument.code)"/>
<label value="Ngày tạo: @load(vm.selectedDocument.date)"/>
<label value="Trạng thái: @load(vm.selectedDocument.status)" style="color:blue"/>

        <separator/>

        <hbox spacing="10px">
            <button label="Phê duyệt" onClick="@command('approve')"/>
            <button label="Chuyển tiếp" onClick="@command('forward')"/>
            <button label="Từ chối" onClick="@command('reject')"/>
        </hbox>
    </vlayout>
</window>

//login.zul
<window apply="org.zkoss.bind.BindComposer"
viewModel="@id('vm') @init('com.example.zkdoc.viewmodel.LoginViewModel')"
title="Đăng nhập" border="normal" width="300px" closable="false"
mode="modal">
<vlayout spacing="10px" style="padding:15px">
<label value="Hệ thống quản lý văn bản" style="font-size:16px; font-weight:bold"/>
<hbox>
<label value="Tài khoản:" width="80px"/>
<textbox value="@bind(vm.username)" width="180px"/>
</hbox>
<hbox>
<label value="Mật khẩu:" width="80px"/>
<textbox type="password" value="@bind(vm.password)" width="180px"/>
</hbox>
<button label="Đăng nhập" onClick="@command('login')" width="100%"/>
</vlayout>
</window>
//timeline.zul
<listbox model="@bind(vm.historyList)" width="100%">
<listhead>
<listheader label="Thời gian" width="160px"/>
<listheader label="Hành động" width="140px"/>
<listheader label="Ghi chú"/>
</listhead>
<template name="model">
<listitem>
<listcell label="@load(each.time)"/>
<listcell label="@load(each.action)"/>
<listcell label="@load(each.note)"/>
</listitem>
</template>
</listbox>

//index.zul
<?page title="ZK Document Workflow" contentType="text/html;charset=UTF-8"?>
<zk>
    <include src="zul/login.zul"/>
</zk>
