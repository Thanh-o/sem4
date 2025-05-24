package com.example.usersevice.dto;



public class DepartmentDTO {
    private Integer id;
    private String department_name;
    private String location;
    private String department_img;
    private String department_description;
    private String summary;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepartment_img() {
        return department_img;
    }

    public void setDepartment_img(String department_img) {
        this.department_img = department_img;
    }

    public String getDepartment_description() {
        return department_description;
    }

    public void setDepartment_description(String department_description) {
        this.department_description = department_description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
