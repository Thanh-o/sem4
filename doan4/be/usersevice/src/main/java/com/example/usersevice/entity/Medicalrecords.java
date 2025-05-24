package com.example.usersevice.entity;



import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicalrecords")
public class Medicalrecords extends Entitys<Integer> {
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patients patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctors doctor;

    @Column(length = 1000)
    private String symptoms;

    @Column(length = 1000)
    private String diagnosis;

    @Column(length = 1000)
    private String treatment;

    @Column(length = 1000)
    private String prescription;

    private LocalDateTime follow_up_date;

    private BigDecimal severity;

    private String image;

    public Medicalrecords() {
    }

    // Getters and Setters
    public Patients getPatient() {
        return patient;
    }

    public void setPatient(Patients patient) {
        this.patient = patient;
    }

    public Doctors getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctors doctor) {
        this.doctor = doctor;
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
