package com.example.usersevice.service;

import com.example.usersevice.dto.DoctorDTO;
import com.example.usersevice.dto.LoginRequestDTO;

import java.util.List;
import java.util.Map;

public interface DoctorsService {

    void insert(DoctorDTO doctorDTO);

    void update(DoctorDTO doctorDTO);

    void delete(Integer id);

    List<DoctorDTO> list();

    List<DoctorDTO> search(DoctorDTO doctorDTO);

    Map<String, Object> login(LoginRequestDTO loginRequest);

    DoctorDTO getById(Integer id);

    List<DoctorDTO> searchByKeyword(String keyword);

    void insertAll(List<DoctorDTO> doctorDTOs);
}