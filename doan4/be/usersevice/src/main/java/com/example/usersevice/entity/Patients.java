package com.example.usersevice.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patients extends Entitys<Integer> {
    @Column(nullable = false)
    private String patient_name;

    private LocalDateTime patient_dob;

    @Column(unique = true, nullable = false)
    private String patient_email;

    private String patient_phone;

    private String patient_address;

    private String patient_password;

    @Column(unique = true, nullable = false)
    private String patient_username;

    @OneToMany(mappedBy = "patient")
    private List<Medicalrecords> medicalrecordsList;

    private String patient_gender;

    private String patient_code;

    private String patient_img;

    public Patients() {
    }

    // Getters and Setters
    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public LocalDateTime getPatient_dob() {
        return patient_dob;
    }

    public void setPatient_dob(LocalDateTime patient_dob) {
        this.patient_dob = patient_dob;
    }

    public String getPatient_email() {
        return patient_email;
    }

    public void setPatient_email(String patient_email) {
        this.patient_email = patient_email;
    }

    public String getPatient_phone() {
        return patient_phone;
    }

    public void setPatient_phone(String patient_phone) {
        this.patient_phone = patient_phone;
    }

    public String getPatient_address() {
        return patient_address;
    }

    public void setPatient_address(String patient_address) {
        this.patient_address = patient_address;
    }

    public String getPatient_password() {
        return patient_password;
    }

    public void setPatient_password(String patient_password) {
        this.patient_password = patient_password;
    }

    public String getPatient_username() {
        return patient_username;
    }

    public void setPatient_username(String patient_username) {
        this.patient_username = patient_username;
    }

    public List<Medicalrecords> getMedicalrecordsList() {
        return medicalrecordsList;
    }

    public void setMedicalrecordsList(List<Medicalrecords> medicalrecordsList) {
        this.medicalrecordsList = medicalrecordsList;
    }

    public String getPatient_gender() {
        return patient_gender;
    }

    public void setPatient_gender(String patient_gender) {
        this.patient_gender = patient_gender;
    }

    public String getPatient_code() {
        return patient_code;
    }

    public void setPatient_code(String patient_code) {
        this.patient_code = patient_code;
    }

    public String getPatient_img() {
        return patient_img;
    }

    public void setPatient_img(String patient_img) {
        this.patient_img = patient_img;
    }
}
