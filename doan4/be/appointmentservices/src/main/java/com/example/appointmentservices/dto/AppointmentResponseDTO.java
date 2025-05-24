package com.example.appointmentservices.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class AppointmentResponseDTO {
    private Integer appointmentId;
    private Integer patientId;
    private Integer doctorId;
    private Date appointmentDate;
    private Date medicalDay;
    private Integer slot;
    private String status;
    private String paymentName;
    private BigDecimal price;
    private Integer staffId;
    private Map<String, Object> patient;
    private Map<String, Object> doctor;
    private Map<String, Object> staff;

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getMedicalDay() {
        return medicalDay;
    }

    public void setMedicalDay(Date medicalDay) {
        this.medicalDay = medicalDay;
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Map<String, Object> getPatient() {
        return patient;
    }

    public void setPatient(Map<String, Object> patient) {
        this.patient = patient;
    }

    public Map<String, Object> getDoctor() {
        return doctor;
    }

    public void setDoctor(Map<String, Object> doctor) {
        this.doctor = doctor;
    }

    public Map<String, Object> getStaff() {
        return staff;
    }

    public void setStaff(Map<String, Object> staff) {
        this.staff = staff;
    }
}