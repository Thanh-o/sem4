package com.example.appointmentservices.controller;


import com.example.appointmentservices.dto.AppointmentRequestDTO;
import com.example.appointmentservices.dto.AppointmentResponseDTO;
import com.example.appointmentservices.dto.LockedSlotResponseDTO;
import com.example.appointmentservices.dto.SlotLockRequestDTO;
import com.example.appointmentservices.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentsController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody AppointmentRequestDTO requestDTO) {
        try {
            appointmentService.insertAppointment(requestDTO);
            return ResponseEntity.ok("Appointment created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating appointment: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody AppointmentRequestDTO requestDTO) {
        try {
            appointmentService.updateAppointment(requestDTO);
            return ResponseEntity.ok("Appointment updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating appointment: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody AppointmentRequestDTO requestDTO) {
        try {
            appointmentService.deleteAppointment(requestDTO);
            return ResponseEntity.ok("Appointment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting appointment: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<AppointmentResponseDTO>> list() {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentDetails(@PathVariable int appointmentId) {
        try {
            AppointmentResponseDTO appointment = appointmentService.getAppointmentDetails(appointmentId);
            if (appointment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/lock-slot")
    public ResponseEntity<?> lockSlot(@RequestBody SlotLockRequestDTO slotDTO) {
        try {
            appointmentService.lockSlot(slotDTO);
            return ResponseEntity.ok("Slot locked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(409).body("Error locking slot: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody SlotLockRequestDTO slotDTO) {
        try {
            appointmentService.confirmPayment(slotDTO);
            return ResponseEntity.ok("Payment confirmed and slot unlocked");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error confirming payment: " + e.getMessage());
        }
    }

    @GetMapping("/check-locked-slots")
    public ResponseEntity<List<LockedSlotResponseDTO>> checkLockedSlots(
            @RequestParam("doctorId") String doctorId,
            @RequestParam("date") String date) {
        try {
            List<LockedSlotResponseDTO> lockedSlots = appointmentService.checkLockedSlots(doctorId, date);
            return ResponseEntity.ok(lockedSlots);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/today")
    public ResponseEntity<List<AppointmentResponseDTO>> getTodaysAppointments(@RequestParam int doctorId) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getTodaysAppointments(doctorId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
