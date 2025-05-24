package com.example.usersevice.entity;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "departments")
public class Departments extends Entitys<Integer> {
    @Column(nullable = false)
    private String department_name;

    private String location;

    private String department_img;

    private String department_description;

    private String summary;

    @OneToMany(mappedBy = "department")
    private List<Doctors> doctorsList;

    public Departments() {
    }

    // Getters and Setters
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

    public List<Doctors> getDoctorsList() {
        return doctorsList;
    }

    public void setDoctorsList(List<Doctors> doctorsList) {
        this.doctorsList = doctorsList;
    }
}
