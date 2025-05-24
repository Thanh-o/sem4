package com.example.usersevice.dto;



import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MedicalrecordDTO {
    private Integer id;
    private Integer patient_id;
    private Integer doctor_id;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private LocalDateTime follow_up_date;
    private BigDecimal severity;
    private String image;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Integer patient_id) {
        this.patient_id = patient_id;
    }

    public Integer getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Integer doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public LocalDateTime getFollow_up_date() {
        return follow_up_date;
    }

    public void setFollow_up_date(LocalDateTime follow_up_date) {
        this.follow_up_date = follow_up_date;
    }

    public BigDecimal getSeverity() {
        return severity;
    }

    public void setSeverity(BigDecimal severity) {
        this.severity = severity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
