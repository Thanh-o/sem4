package com.example.appointmentservices.service;



import com.example.appointmentservices.dto.*;

import java.util.List;
import java.util.Map;

public interface AppointmentService {
    void insertAppointment(AppointmentRequestDTO requestDTO);
    void updateAppointment(AppointmentRequestDTO requestDTO);
    void deleteAppointment(AppointmentRequestDTO requestDTO);
    List<AppointmentResponseDTO> getAllAppointments();
    List<AppointmentResponseDTO> getByField(Map<String, String> criteria);
    List<AppointmentResponseDTO> getAppointmentsByDoctorId(int doctorId);
    void insertAllAppointments(List<AppointmentRequestDTO> requestDTOs);
    void updateStatus(AppointmentRequestDTO requestDTO);
    List<String> getAppointmentFields();
    void sendEmail(EmailRequestDTO emailDTO);
    void sendEmailToDoctor(EmailRequestDTO emailDTO);
    void lockSlot(SlotLockRequestDTO slotDTO);
    void confirmPayment(SlotLockRequestDTO slotDTO);
    List<LockedSlotResponseDTO> checkLockedSlots(String doctorId, String date);
    List<AppointmentResponseDTO> getTodaysAppointments(int doctorId);
    List<AppointmentResponseDTO> searchAppointments(String startDate, String endDate, String status);
    AppointmentResponseDTO getAppointmentDetails(int appointmentId);
    List<AppointmentResponseDTO> searchByCriteriaAndDoctor(String startDate, String endDate, String status, int doctorId);
}