package com.example.appointmentservices.service.Impl;


import com.example.appointmentservices.dto.*;
import com.example.appointmentservices.entity.Appointments;
import com.example.appointmentservices.repository.AppointmentRepository;
import com.example.appointmentservices.service.AppointmentLockManager;
import com.example.appointmentservices.service.AppointmentService;
import com.example.appointmentservices.service.SendEmailUsername;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AppointmentImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentLockManager appointmentLockManager;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SendEmailUsername sendEmail;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentImpl.class);

    @Autowired
    private HttpServletRequest request;

    private final ModelMapper modelMapper = new ModelMapper();

    private static final String USER_SERVICE_URL = "http://localhost:9090/api/v1";

    private HttpEntity<?> createHttpEntityWithJwt(Object body) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("JWT token is missing in request header");
        }
        token = token.substring(7); // Bỏ "Bearer "
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return body != null ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);
    }

    @Override
    public void insertAppointment(AppointmentRequestDTO requestDTO) {
        try {
            String patientEmail = requestDTO.getPatientEmail();
            if (patientEmail == null || patientEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("Patient email is required");
            }
            logger.info("Checking patient with email: {}", patientEmail);

            Integer patientId = null;
            ResponseEntity<Map> patientResponse = restTemplate.exchange(
                    USER_SERVICE_URL + "/patients/email?email=" + patientEmail,
                    HttpMethod.GET,
                    createHttpEntityWithJwt(null),
                    Map.class
            );

            if (patientResponse.getStatusCode().is2xxSuccessful() && patientResponse.getBody() != null) {
                Map<String, Object> responseBody = patientResponse.getBody();
                logger.info("Response from user-service: {}", responseBody);

                if (responseBody.containsKey("patientId")) {
                    patientId = (Integer) responseBody.get("patientId");
                } else if (responseBody.containsKey("id")) {
                    patientId = (Integer) responseBody.get("id");
                } else if (responseBody.containsKey("patient_id")) {
                    patientId = (Integer) responseBody.get("patient_id");
                }

                if (patientId != null) {
                    logger.info("Found patient with ID: {}", patientId);
                } else {
                    logger.warn("No patient ID found in response: {}", responseBody);
                }
            } else {
                logger.info("Patient not found for email: {}. Status: {}. Creating new patient.",
                        patientEmail, patientResponse.getStatusCode());
            }

            if (patientId == null) {
                String generatedPassword = RandomStringUtils.randomAlphanumeric(8);
                PatientCreationDTO patientDTO = new PatientCreationDTO();
                patientDTO.setPatientName(patientEmail.split("@")[0]);
                patientDTO.setPatientEmail(patientEmail);
                patientDTO.setPatientPassword(generatedPassword);

                ResponseEntity<Map> createPatientResponse = restTemplate.exchange(
                        USER_SERVICE_URL + "/patients/insert",
                        HttpMethod.POST,
                        createHttpEntityWithJwt(patientDTO),
                        Map.class
                );

                if (createPatientResponse.getStatusCode().is2xxSuccessful() && createPatientResponse.getBody() != null) {
                    Map<String, Object> createResponseBody = createPatientResponse.getBody();
                    logger.info("Create patient response: {}", createResponseBody);

                    if (createResponseBody.containsKey("patientId")) {
                        patientId = (Integer) createResponseBody.get("patientId");
                    } else if (createResponseBody.containsKey("id")) {
                        patientId = (Integer) createResponseBody.get("id");
                    } else if (createResponseBody.containsKey("patient_id")) {
                        patientId = (Integer) createResponseBody.get("patient_id");
                    }

                    if (patientId != null) {
                        logger.info("Created new patient with ID: {}", patientId);
                        sendEmail.sendEmail(
                                patientDTO.getPatientName(),
                                patientDTO.getPatientEmail(),
                                "Tài khoản của bạn đã được tạo. Mật khẩu: " + generatedPassword
                        );
                        logger.info("Sent email to: {}", patientEmail);
                    } else {
                        logger.error("No patient ID in create response: {}", createResponseBody);
                        throw new RuntimeException("Failed to obtain patient ID from user-service");
                    }
                } else {
                    logger.error("Failed to create patient. Status: {}, Body: {}",
                            createPatientResponse.getStatusCode(), createPatientResponse.getBody());
                    throw new RuntimeException("Failed to create patient in user-service");
                }
            }

            if (patientId == null) {
                logger.error("Patient ID could not be obtained for email: {}", patientEmail);
                throw new IllegalArgumentException("Patient information is required");
            }

            // Ánh xạ thủ công thay vì dùng ModelMapper
            Appointments appointment = new Appointments();
            appointment.setPatientId(patientId);
            appointment.setDoctorId(requestDTO.getDoctorId());
            appointment.setStaffId(requestDTO.getStaffId());
            appointment.setAppointmentDate(requestDTO.getAppointmentDate());
            appointment.setMedicalDay(requestDTO.getMedicalDay());
            appointment.setSlot(requestDTO.getSlot());
            appointment.setStatus(requestDTO.getStatus());
            appointment.setPaymentName(requestDTO.getPaymentName());
            appointment.setPrice(requestDTO.getPrice());

            appointmentRepository.save(appointment);
            logger.info("Appointment created successfully for patient ID: {}", patientId);

        } catch (HttpClientErrorException e) {
            logger.error("HTTP error from user-service: Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error communicating with user-service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating appointment: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating appointment: " + e.getMessage());
        }
    }
    @Override
    public void updateAppointment(AppointmentRequestDTO requestDTO) {
        try {
            Appointments appointment = modelMapper.map(requestDTO, Appointments.class);
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new RuntimeException("Error updating appointment: " + e.getMessage());
        }
    }

    @Override
    public void deleteAppointment(AppointmentRequestDTO requestDTO) {
        try {
            Appointments appointment = modelMapper.map(requestDTO, Appointments.class);
            appointmentRepository.delete(appointment);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting appointment: " + e.getMessage());
        }
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppointments() {
        try {
            List<Appointments> appointments = appointmentRepository.findAll();
            return mapAppointmentsToResponseDTOs(appointments);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving appointments: " + e.getMessage());
        }
    }

    @Override
    public List<AppointmentResponseDTO> getByField(Map<String, String> criteria) {
        try {
            List<Appointments> appointments = appointmentRepository.findAll();
            List<Appointments> filtered = appointments.stream()
                    .filter(a -> matchesCriteria(a, criteria))
                    .collect(Collectors.toList());
            return mapAppointmentsToResponseDTOs(filtered);
        } catch (Exception e) {
            throw new RuntimeException("Error searching appointments: " + e.getMessage());
        }
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByDoctorId(int doctorId) {
        try {
            List<Appointments> appointments = appointmentRepository.findByDoctorId(doctorId);
            return mapAppointmentsToResponseDTOs(appointments);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving appointments by doctor: " + e.getMessage());
        }
    }

    @Override
    public void insertAllAppointments(List<AppointmentRequestDTO> requestDTOs) {
        try {
            for (AppointmentRequestDTO requestDTO : requestDTOs) {
                insertAppointment(requestDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inserting multiple appointments: " + e.getMessage());
        }
    }

    @Override
    public void updateStatus(AppointmentRequestDTO requestDTO) {
        try {
            Appointments appointment = modelMapper.map(requestDTO, Appointments.class);
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new RuntimeException("Error updating appointment status: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAppointmentFields() {
        Field[] fields = Appointments.class.getDeclaredFields();
        return Arrays.stream(fields)
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void sendEmail(EmailRequestDTO emailDTO) {
        try {
            sendEmail.sendEmailFormRegister(
                    emailDTO.getDoctorName(),
                    emailDTO.getDepartmentName(),
                    emailDTO.getMedicalDay(),
                    emailDTO.getPatientEmail(),
                    emailDTO.getPatientName(),
                    emailDTO.getTimeSlot()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailToDoctor(EmailRequestDTO emailDTO) {
        try {
            sendEmail.sendEmailToDoctor(
                    emailDTO.getDoctorName(),
                    emailDTO.getDepartmentName(),
                    emailDTO.getMedicalDay(),
                    emailDTO.getDoctorEmail(),
                    emailDTO.getPatientName(),
                    emailDTO.getTimeSlot()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error sending email to doctor: " + e.getMessage());
        }
    }

    @Override
    public void lockSlot(SlotLockRequestDTO slotDTO) {
        try {
            String doctorId = slotDTO.getDoctorId();
            String date = slotDTO.getDate();
            String time = slotDTO.getTime();

            if (appointmentLockManager.isSlotLocked(doctorId, date, time)) {
                throw new RuntimeException("Slot already locked");
            }

            appointmentLockManager.lockSlot(doctorId, date, time);
        } catch (Exception e) {
            throw new RuntimeException("Error locking slot: " + e.getMessage());
        }
    }

    @Override
    public void confirmPayment(SlotLockRequestDTO slotDTO) {
        try {
            String doctorId = slotDTO.getDoctorId();
            String date = slotDTO.getDate();
            String time = slotDTO.getTime();

            appointmentLockManager.confirmPayment(doctorId, date, time);
        } catch (Exception e) {
            throw new RuntimeException("Error confirming payment: " + e.getMessage());
        }
    }

    @Override
    public List<LockedSlotResponseDTO> checkLockedSlots(String doctorId, String date) {
        List<LockedSlotResponseDTO> lockedSlots = new ArrayList<>();

        for (int i = 8; i <= 17; i++) {
            String time = String.format("%02d:00", i);
            if (appointmentLockManager.isSlotLocked(doctorId, date, time)) {
                LockedSlotResponseDTO slotDTO = new LockedSlotResponseDTO();
                slotDTO.setTime(time);
                lockedSlots.add(slotDTO);
            }
        }

        return lockedSlots;
    }

    @Override
    public List<AppointmentResponseDTO> getTodaysAppointments(int doctorId) {
        try {
            LocalDate today = LocalDate.now();
            List<Appointments> appointments = appointmentRepository.findByDoctorId(doctorId);
            List<AppointmentResponseDTO> result = new ArrayList<>();

            for (Appointments appointment : appointments) {
                LocalDate appointmentDate = appointment.getMedicalDay().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                if (appointmentDate.isEqual(today)) {
                    AppointmentResponseDTO responseDTO = modelMapper.map(appointment, AppointmentResponseDTO.class);

                    // Lấy thông tin patient từ User service
                    if (appointment.getPatientId() != null) {
                        ResponseEntity<Map> patientResponse = restTemplate.exchange(
                                USER_SERVICE_URL + "/patients/" + appointment.getPatientId(),
                                HttpMethod.GET,
                                createHttpEntityWithJwt(null),
                                Map.class
                        );
                        if (patientResponse.getStatusCode().is2xxSuccessful()) {
                            responseDTO.setPatient(patientResponse.getBody());
                        }
                    }

                    result.add(responseDTO);
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving today's appointments: " + e.getMessage());
        }
    }

    @Override
    public List<AppointmentResponseDTO> searchAppointments(String startDate, String endDate, String status) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = startDate != null ? sdf.parse(startDate) : null;
            Date end = endDate != null ? sdf.parse(endDate) : null;

            List<Appointments> appointments = appointmentRepository.findByCriteria(start, end, status);
            return mapAppointmentsToResponseDTOs(appointments);
        } catch (Exception e) {
            throw new RuntimeException("Error searching appointments: " + e.getMessage());
        }
    }

    @Override
    public AppointmentResponseDTO getAppointmentDetails(int appointmentId) {
        try {
            Appointments appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            AppointmentResponseDTO responseDTO = modelMapper.map(appointment, AppointmentResponseDTO.class);

            // Lấy thông tin patient từ User service
            if (appointment.getPatientId() != null) {
                ResponseEntity<Map> patientResponse = restTemplate.exchange(
                        USER_SERVICE_URL + "/patients/" + appointment.getPatientId(),
                        HttpMethod.GET,
                        createHttpEntityWithJwt(null),
                        Map.class
                );
                if (patientResponse.getStatusCode().is2xxSuccessful()) {
                    responseDTO.setPatient(patientResponse.getBody());
                }
            }

            // Lấy thông tin doctor từ User service
            if (appointment.getDoctorId() != null) {
                ResponseEntity<Map> doctorResponse = restTemplate.exchange(
                        USER_SERVICE_URL + "/doctors/" + appointment.getDoctorId(),
                        HttpMethod.GET,
                        createHttpEntityWithJwt(null),
                        Map.class
                );
                if (doctorResponse.getStatusCode().is2xxSuccessful()) {
                    responseDTO.setDoctor(doctorResponse.getBody());
                }
            }

            return responseDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving appointment details: " + e.getMessage());
        }
    }

    @Override
    public List<AppointmentResponseDTO> searchByCriteriaAndDoctor(String startDate, String endDate, String status, int doctorId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = startDate != null ? sdf.parse(startDate) : null;
            Date end = endDate != null ? sdf.parse(endDate) : null;

            List<Appointments> appointments = appointmentRepository.findByCriteriaAndDoctor(start, end, status, doctorId);
            return mapAppointmentsToResponseDTOs(appointments);
        } catch (Exception e) {
            throw new RuntimeException("Error searching appointments by criteria and doctor: " + e.getMessage());
        }
    }

    private List<AppointmentResponseDTO> mapAppointmentsToResponseDTOs(List<Appointments> appointments) {
        List<AppointmentResponseDTO> result = new ArrayList<>();

        for (Appointments appointment : appointments) {
            AppointmentResponseDTO responseDTO = modelMapper.map(appointment, AppointmentResponseDTO.class);

            // Lấy thông tin patient từ User service
            if (appointment.getPatientId() != null) {
                ResponseEntity<Map> patientResponse = restTemplate.exchange(
                        USER_SERVICE_URL + "/patients/" + appointment.getPatientId(),
                        HttpMethod.GET,
                        createHttpEntityWithJwt(null),
                        Map.class
                );
                if (patientResponse.getStatusCode().is2xxSuccessful()) {
                    responseDTO.setPatient(patientResponse.getBody());
                }
            }

            // Lấy thông tin doctor từ User service
            if (appointment.getDoctorId() != null) {
                ResponseEntity<Map> doctorResponse = restTemplate.exchange(
                        USER_SERVICE_URL + "/doctors/" + appointment.getDoctorId(),
                        HttpMethod.GET,
                        createHttpEntityWithJwt(null),
                        Map.class
                );
                if (doctorResponse.getStatusCode().is2xxSuccessful()) {
                    responseDTO.setDoctor(doctorResponse.getBody());
                }
            }

            // Lấy thông tin staff từ User service
            if (appointment.getStaffId() != null) {
                ResponseEntity<Map> staffResponse = restTemplate.exchange(
                        USER_SERVICE_URL + "/staffs/" + appointment.getStaffId(),
                        HttpMethod.GET,
                        createHttpEntityWithJwt(null),
                        Map.class
                );
                if (staffResponse.getStatusCode().is2xxSuccessful()) {
                    responseDTO.setStaff(staffResponse.getBody());
                }
            }

            result.add(responseDTO);
        }

        return result;
    }

    private boolean matchesCriteria(Appointments appointment, Map<String, String> criteria) {
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("status") && !appointment.getStatus().equals(value)) {
                return false;
            }
        }
        return true;
    }
}