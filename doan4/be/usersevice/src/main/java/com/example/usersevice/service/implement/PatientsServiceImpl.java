package com.example.usersevice.service.implement;

import com.example.usersevice.dto.ChangePasswordRequestDTO;
import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.dto.PatientDTO;
import com.example.usersevice.dto.RegisterRequestDTO;
import com.example.usersevice.entity.Patients;
import com.example.usersevice.repository.PatientsRepository;
import com.example.usersevice.security.JwtUtil;
import com.example.usersevice.service.PatientsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientsServiceImpl implements PatientsService {

    @Autowired
    private PatientsRepository patientsRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void insert(PatientDTO patientDTO) {
        Patients patient = modelMapper.map(patientDTO, Patients.class);
        patientsRepository.save(patient);
    }

    @Override
    public void delete(Integer id) {
        patientsRepository.deleteById(id);
    }

    @Override
    public Optional<PatientDTO> findByEmail(String email) {
        return patientsRepository.findByEmail(email)
                .map(patient -> {
                    PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
                    dto.setPatient_id(patient.getId()); // ánh xạ thủ công
                    return dto;
                });
    }


    @Override
    public List<PatientDTO> list() {
        return patientsRepository.findAll().stream()
                .map(p -> {
                    PatientDTO dto = modelMapper.map(p, PatientDTO.class);
                    dto.setPatient_id(p.getId()); // Đã thêm để gán patient_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientDTO> search(PatientDTO patientDTO) {
        List<Patients> patients = new ArrayList<>();

        if (patientDTO.getPatient_id() != null) {
            patientsRepository.findById(patientDTO.getPatient_id()).ifPresent(patients::add);
        } else if (patientDTO.getPatient_email() != null) {
            patientsRepository.findByPatientEmail(patientDTO.getPatient_email()).ifPresent(patients::add);
        } else if (patientDTO.getPatient_username() != null) {
            patientsRepository.findByPatientUsername(patientDTO.getPatient_username()).ifPresent(patients::add);
        } else {
            patients = patientsRepository.findAll();
        }

        return patients.stream()
                .map(p -> {
                    PatientDTO dto = modelMapper.map(p, PatientDTO.class);
                    dto.setPatient_id(p.getId()); // Đã thêm để gán patient_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(LoginRequestDTO loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            Patients patient = patientsRepository.findByPatientUsername(loginRequest.getUsername()).get();

            String token = jwtUtil.generateToken(patient.getPatient_username());

            Map<String, Object> response = new HashMap<>();
            response.put("patient_name", patient.getPatient_name());
            response.put("patient_id", patient.getId());
            response.put("token", token);
            return response;

        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Map<String, Object> googleLogin(RegisterRequestDTO request) {
        Optional<Patients> patientOpt = patientsRepository.findByPatientEmail(request.getPatient_email());
        Patients patient;
        if (patientOpt.isEmpty()) {
            patient = modelMapper.map(request, Patients.class);
            patient.setPatient_username(request.getPatient_email());
            patient.setPatient_password(UUID.randomUUID().toString());
            patient = patientsRepository.save(patient);
        } else {
            patient = patientOpt.get();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("patient_name", patient.getPatient_name());
        response.put("patient_id", patient.getId()); // Đã có patient_id
        return response;
    }

    @Override
    public Map<String, Object> facebookLogin(Map<String, String> request) {
        String accessToken = request.get("accessToken");
        String userID = request.get("userID");
        String facebookUrl = "https://graph.facebook.com/" + userID + "?fields=id,name,email&access_token=" + accessToken;
        ResponseEntity<Map> response = restTemplate.getForEntity(facebookUrl, Map.class);
        if (response.getStatusCodeValue() != 200) {
            throw new IllegalArgumentException("Failed to authenticate with Facebook");
        }
        Map<String, Object> userInfo = response.getBody();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        Optional<Patients> patientOpt = patientsRepository.findByPatientEmail(email);
        Patients patient;
        if (patientOpt.isEmpty()) {
            patient = new Patients();
            patient.setPatient_name(name);
            patient.setPatient_email(email);
            patient.setPatient_username(email);
            patient.setPatient_password(UUID.randomUUID().toString());
            patient = patientsRepository.save(patient);
        } else {
            patient = patientOpt.get();
        }
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("patient_username", patient.getPatient_name());
        responseBody.put("patient_id", patient.getId()); // Đã có patient_id
        return responseBody;
    }

    @Override
    public void insertAll(List<PatientDTO> patientDTOs) {
        List<Patients> patients = patientDTOs.stream()
                .map(dto -> modelMapper.map(dto, Patients.class))
                .collect(Collectors.toList());
        List<Patients> savedPatients = patientsRepository.saveAll(patients);
        // Cập nhật DTO với patient_id sau khi lưu
        for (int i = 0; i < patientDTOs.size(); i++) {
            patientDTOs.get(i).setPatient_id(savedPatients.get(i).getId()); // Đã thêm để gán patient_id
        }
    }

    @Override
    public Map<String, Object> register(RegisterRequestDTO registerRequest) {
        if (patientsRepository.findByPatientEmail(registerRequest.getPatient_email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }
        Patients patient = modelMapper.map(registerRequest, Patients.class);
        patient.setPatient_username(registerRequest.getPatient_email());
        patient.setPatient_password(passwordEncoder.encode(registerRequest.getPatient_password()));
        patient = patientsRepository.save(patient);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful.");
        response.put("patient_id", patient.getId());
        response.put("patient_username", patient.getPatient_username());
        return response;
    }


    @Override
    public void changePassword(ChangePasswordRequestDTO request) {
        Patients patient = patientsRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        if (!request.getCurrentPassword().equals(patient.getPatient_password())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        patient.setPatient_password(request.getNewPassword());
        patientsRepository.save(patient);
    }

    @Override
    public void update(PatientDTO patientDTO) {
        Patients patient = patientsRepository.findById(patientDTO.getPatient_id())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        // Chỉ cập nhật các trường không null từ patientDTO
        if (patientDTO.getPatient_name() != null) {
            patient.setPatient_name(patientDTO.getPatient_name());
        }
        if (patientDTO.getPatient_dob() != null) {
            patient.setPatient_dob(patientDTO.getPatient_dob());
        }
        if (patientDTO.getPatient_email() != null) {
            Optional<Patients> existingEmail = patientsRepository.findByPatientEmail(patientDTO.getPatient_email());
            if (existingEmail.isPresent() && !existingEmail.get().getId().equals(patient.getId())) {
                throw new IllegalArgumentException("Email already exists.");
            }
            patient.setPatient_email(patientDTO.getPatient_email());
        }
        if (patientDTO.getPatient_phone() != null) {
            patient.setPatient_phone(patientDTO.getPatient_phone());
        }
        if (patientDTO.getPatient_address() != null) {
            patient.setPatient_address(patientDTO.getPatient_address());
        }
        if (patientDTO.getPatient_password() != null) {
            patient.setPatient_password(patientDTO.getPatient_password());
        }
        if (patientDTO.getPatient_username() != null) {
            Optional<Patients> existingUsername = patientsRepository.findByPatientUsername(patientDTO.getPatient_username());
            if (existingUsername.isPresent() && !existingUsername.get().getId().equals(patient.getId())) {
                throw new IllegalArgumentException("Username already exists.");
            }
            patient.setPatient_username(patientDTO.getPatient_username());
        }
        if (patientDTO.getPatient_gender() != null) {
            patient.setPatient_gender(patientDTO.getPatient_gender());
        }
        if (patientDTO.getPatient_code() != null) {
            patient.setPatient_code(patientDTO.getPatient_code());
        }
        if (patientDTO.getPatient_img() != null) {
            patient.setPatient_img(patientDTO.getPatient_img());
        }

        patientsRepository.save(patient);
    }

    @Override
    public Map<String, String> uploadImage(MultipartFile image, Integer patientId) throws IOException {
        String uploadDir = "Uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, image.getBytes());

        Patients patient = patientsRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        patient.setPatient_img(filePath.toString());
        patientsRepository.save(patient);

        Map<String, String> response = new HashMap<>();
        response.put("filePath", filePath.toString());
        return response;
    }

    @Override
    public List<PatientDTO> searchByKeyword(String keyword) {
        return patientsRepository.searchByKeyword(keyword).stream()
                .map(p -> {
                    PatientDTO dto = modelMapper.map(p, PatientDTO.class);
                    dto.setPatient_id(p.getId()); // Đã thêm để gán patient_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PatientDTO getById(Integer id) {
        Patients patient = patientsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
        dto.setPatient_id(patient.getId()); // Đã thêm để gán patient_id
        return dto;
    }
}