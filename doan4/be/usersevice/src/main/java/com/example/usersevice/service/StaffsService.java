package com.example.usersevice.service;

import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.dto.StaffDTO;

import java.util.List;

public interface StaffsService {

    void insert(StaffDTO staffDTO);

    void update(StaffDTO staffDTO);

    void delete(Integer id);

    List<StaffDTO> list();

    List<StaffDTO> search(StaffDTO staffDTO);

    StaffDTO loginAdmin(LoginRequestDTO loginRequest);

    StaffDTO loginStaff(LoginRequestDTO loginRequest);

    StaffDTO getById(Integer id);

    List<StaffDTO> searchByKeyword(String keyword);

    void insertAll(List<StaffDTO> staffDTOs);
}