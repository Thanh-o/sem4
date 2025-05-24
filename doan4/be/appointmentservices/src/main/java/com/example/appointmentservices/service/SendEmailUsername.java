package com.example.appointmentservices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendEmailUsername {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String patientName, String patientEmail, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(patientEmail);
            mailMessage.setSubject("Appointment Confirmation");
            mailMessage.setText("Dear " + patientName + ",\n\n" + message + "\n\nBest regards,\nAppointment Service");
            mailMessage.setFrom("no-reply@appointmentservice.com");
            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public void sendEmailFormRegister(String doctorName, String departmentName, String medicalDay, String patientEmail, String patientName, String timeSlot) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(patientEmail);
            mailMessage.setSubject("Appointment Registration Confirmation");
            mailMessage.setText("Dear " + patientName + ",\n\n" +
                    "Your appointment has been registered.\n" +
                    "Doctor: " + doctorName + "\n" +
                    "Department: " + departmentName + "\n" +
                    "Date: " + medicalDay + "\n" +
                    "Time: " + timeSlot + "\n\n" +
                    "Best regards,\nAppointment Service");
            mailMessage.setFrom("no-reply@appointmentservice.com");
            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error sending registration email: " + e.getMessage());
        }
    }

    public void sendEmailToDoctor(String doctorName, String departmentName, String appointmentDate, String doctorEmail, String patientName, String timeSlot) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(doctorEmail);
            mailMessage.setSubject("New Appointment Notification");
            mailMessage.setText("Dear Dr. " + doctorName + ",\n\n" +
                    "A new appointment has been scheduled.\n" +
                    "Patient: " + patientName + "\n" +
                    "Department: " + departmentName + "\n" +
                    "Date: " + appointmentDate + "\n" +
                    "Time: " + timeSlot + "\n\n" +
                    "Best regards,\nAppointment Service");
            mailMessage.setFrom("no-reply@appointmentservice.com");
            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email to doctor: " + e.getMessage());
        }
    }
}
