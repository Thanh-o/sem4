package com.example.usersevice.service.impl;

import com.example.usersevice.dto.DepartmentDTO;
import com.example.usersevice.dto.DoctorDTO;
import com.example.usersevice.entity.Departments;
import com.example.usersevice.repository.DepartmentsRepository;
import com.example.usersevice.repository.DoctorsRepository;
import com.example.usersevice.service.DepartmentsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentsServiceImpl implements DepartmentsService {

    @Autowired
    private DepartmentsRepository departmentsRepository;

    @Autowired
    private DoctorsRepository doctorsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void insert(DepartmentDTO departmentDTO) {
        Departments department = modelMapper.map(departmentDTO, Departments.class);
        departmentsRepository.save(department);
    }

    @Override
    public void update(DepartmentDTO departmentDTO) {
        Departments department = departmentsRepository.findById(departmentDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found."));
        modelMapper.map(departmentDTO, department);
        departmentsRepository.save(department);
    }

    @Override
    public void delete(Integer id) {
        if (!departmentsRepository.existsById(id)) {
            throw new IllegalArgumentException("Department not found.");
        }
        departmentsRepository.deleteById(id);
    }

    @Override
    public List<DepartmentDTO> list() {
        return departmentsRepository.findAll().stream()
                .map(d -> {
                    DepartmentDTO dto = modelMapper.map(d, DepartmentDTO.class);
                    dto.setId(d.getId()); // Đã thêm để gán id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDTO> search(DepartmentDTO departmentDTO) {
        List<Departments> departments = departmentsRepository.findAll();
        return departments.stream()
                .filter(d -> departmentDTO.getDepartment_name() == null ||
                        d.getDepartment_name().contains(departmentDTO.getDepartment_name()))
                .map(d -> {
                    DepartmentDTO dto = modelMapper.map(d, DepartmentDTO.class);
                    dto.setId(d.getId()); // Đã thêm để gán id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorDTO> getDoctorsByDepartmentId(Integer departmentId) {
        return doctorsRepository.findAll().stream()
                .filter(d -> d.getDepartment() != null && d.getDepartment().getId().equals(departmentId))
                .map(d -> {
                    DoctorDTO dto = modelMapper.map(d, DoctorDTO.class);
                    dto.setDoctor_id(d.getId()); // Đã thêm để gán doctor_id
                    dto.setDepartment_id(d.getDepartment().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void insertAll(List<DepartmentDTO> departmentDTOs) {
        List<Departments> departments = departmentDTOs.stream()
                .map(dto -> modelMapper.map(dto, Departments.class))
                .collect(Collectors.toList());
        List<Departments> savedDepartments = departmentsRepository.saveAll(departments);
        // Cập nhật DTO với id sau khi lưu
        for (int i = 0; i < departmentDTOs.size(); i++) {
            departmentDTOs.get(i).setId(savedDepartments.get(i).getId()); // Đã thêm để gán id
        }
    }
}