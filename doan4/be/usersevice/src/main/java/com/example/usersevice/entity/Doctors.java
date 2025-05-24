package com.example.usersevice.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctors extends Entitys<Integer> {
    @Column(nullable = false)
    private String doctor_name;

    private String doctor_phone;

    private String doctor_address;

    @Column(unique = true)
    private String doctor_email;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Departments department;

    @Column(unique = true)
    private String doctor_username;

    private String doctor_password;

    private String summary;

    private String doctor_image;

    private BigDecimal doctor_price;

    private String doctor_description;

    private String working_status;

    @OneToMany(mappedBy = "doctor")
    private List<Medicalrecords> medicalrecordsList;

    public Doctors() {
    }

    // Getters and Setters
    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_phone() {
        return doctor_phone;
    }

    public void setDoctor_phone(String doctor_phone) {
        this.doctor_phone = doctor_phone;
    }

    public String getDoctor_address() {
        return doctor_address;
    }

    public void setDoctor_address(String doctor_address) {
        this.doctor_address = doctor_address;
    }

    public String getDoctor_email() {
        return doctor_email;
    }

    public void setDoctor_email(String doctor_email) {
        this.doctor_email = doctor_email;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }

    public String getDoctor_username() {
        return doctor_username;
    }

    public void setDoctor_username(String doctor_username) {
        this.doctor_username = doctor_username;
    }

    public String getDoctor_password() {
        return doctor_password;
    }

    public void setDoctor_password(String doctor_password) {
        this.doctor_password = doctor_password;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDoctor_image() {
        return doctor_image;
    }

    public void setDoctor_image(String doctor_image) {
        this.doctor_image = doctor_image;
    }

    public BigDecimal getDoctor_price() {
        return doctor_price;
    }

    public void setDoctor_price(BigDecimal doctor_price) {
        this.doctor_price = doctor_price;
    }

    public String getDoctor_description() {
        return doctor_description;
    }

    public void setDoctor_description(String doctor_description) {
        this.doctor_description = doctor_description;
    }

    public String getWorking_status() {
        return working_status;
    }

    public void setWorking_status(String working_status) {
        this.working_status = working_status;
    }

    public List<Medicalrecords> getMedicalrecordsList() {
        return medicalrecordsList;
    }

    public void setMedicalrecordsList(List<Medicalrecords> medicalrecordsList) {
        this.medicalrecordsList = medicalrecordsList;
    }
}
