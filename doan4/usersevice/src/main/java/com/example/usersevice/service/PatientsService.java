package com.example.usersevice.service;

import com.example.usersevice.dto.ChangePasswordRequestDTO;
import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.dto.PatientDTO;
import com.example.usersevice.dto.RegisterRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PatientsService {

    void insert(PatientDTO patientDTO);

    void delete(Integer id);

    List<PatientDTO> list();

    List<PatientDTO> search(PatientDTO patientDTO);

    Map<String, Object> login(LoginRequestDTO loginRequest);

    Map<String, Object> googleLogin(RegisterRequestDTO request);

    Map<String, Object> facebookLogin(Map<String, String> request);

    void insertAll(List<PatientDTO> patientDTOs);

    Map<String, Object> register(RegisterRequestDTO registerRequest);

    void changePassword(ChangePasswordRequestDTO request);

    void update(PatientDTO patientDTO);

    Map<String, String> uploadImage(MultipartFile image, Integer patientId) throws IOException;

    List<PatientDTO> searchByKeyword(String keyword);

    PatientDTO getById(Integer id);
}