package com.example.usersevice.service;

import com.example.usersevice.dto.DepartmentDTO;
import com.example.usersevice.dto.DoctorDTO;

import java.util.List;

public interface DepartmentsService {

    void insert(DepartmentDTO departmentDTO);

    void update(DepartmentDTO departmentDTO);

    void delete(Integer id);

    List<DepartmentDTO> list();

    List<DepartmentDTO> search(DepartmentDTO departmentDTO);

    List<DoctorDTO> getDoctorsByDepartmentId(Integer departmentId);

    void insertAll(List<DepartmentDTO> departmentDTOs);
}