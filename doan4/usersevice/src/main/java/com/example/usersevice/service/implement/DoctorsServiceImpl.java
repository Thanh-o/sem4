package com.example.usersevice.service.impl;

import com.example.usersevice.dto.DoctorDTO;
import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.entity.Departments;
import com.example.usersevice.entity.Doctors;
import com.example.usersevice.repository.DepartmentsRepository;
import com.example.usersevice.repository.DoctorsRepository;
import com.example.usersevice.service.DoctorsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorsServiceImpl implements DoctorsService {

    @Autowired
    private DoctorsRepository doctorsRepository;

    @Autowired
    private DepartmentsRepository departmentsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void insert(DoctorDTO doctorDTO) {
        Doctors doctor = modelMapper.map(doctorDTO, Doctors.class);
        if (doctorDTO.getDepartment_id() != null) {
            Departments department = departmentsRepository.findById(doctorDTO.getDepartment_id())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found."));
            doctor.setDepartment(department);
        }
        doctorsRepository.save(doctor);
    }

    @Override
    public void update(DoctorDTO doctorDTO) {
        Doctors doctor = doctorsRepository.findById(doctorDTO.getDoctor_id())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        // Chỉ cập nhật các trường không null từ doctorDTO
        if (doctorDTO.getDoctor_name() != null) {
            doctor.setDoctor_name(doctorDTO.getDoctor_name());
        }
        if (doctorDTO.getDoctor_phone() != null) {
            doctor.setDoctor_phone(doctorDTO.getDoctor_phone());
        }
        if (doctorDTO.getDoctor_address() != null) {
            doctor.setDoctor_address(doctorDTO.getDoctor_address());
        }
        if (doctorDTO.getDoctor_email() != null) {
            Optional<Doctors> existingEmail = doctorsRepository.findByDoctorEmail(doctorDTO.getDoctor_email());
            if (existingEmail.isPresent() && !existingEmail.get().getId().equals(doctor.getId())) {
                throw new IllegalArgumentException("Email already exists.");
            }
            doctor.setDoctor_email(doctorDTO.getDoctor_email());
        }
        if (doctorDTO.getDoctor_username() != null) {
            Optional<Doctors> existingUsername = doctorsRepository.findByDoctorUsername(doctorDTO.getDoctor_username());
            if (existingUsername.isPresent() && !existingUsername.get().getId().equals(doctor.getId())) {
                throw new IllegalArgumentException("Username already exists.");
            }
            doctor.setDoctor_username(doctorDTO.getDoctor_username());
        }
        if (doctorDTO.getDoctor_password() != null) {
            doctor.setDoctor_password(doctorDTO.getDoctor_password());
        }
        if (doctorDTO.getSummary() != null) {
            doctor.setSummary(doctorDTO.getSummary());
        }
        if (doctorDTO.getDoctor_image() != null) {
            doctor.setDoctor_image(doctorDTO.getDoctor_image());
        }
        if (doctorDTO.getDoctor_price() != null) {
            doctor.setDoctor_price(doctorDTO.getDoctor_price());
        }
        if (doctorDTO.getDoctor_description() != null) {
            doctor.setDoctor_description(doctorDTO.getDoctor_description());
        }
        if (doctorDTO.getWorking_status() != null) {
            doctor.setWorking_status(doctorDTO.getWorking_status());
        }
        if (doctorDTO.getDepartment_id() != null) {
            Departments department = departmentsRepository.findById(doctorDTO.getDepartment_id())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found."));
            doctor.setDepartment(department);
        } else if (doctorDTO.getDepartment_id() == null && doctor.getDepartment() != null) {
            doctor.setDepartment(null); // Xóa liên kết department nếu department_id là null
        }

        doctorsRepository.save(doctor);
    }

    @Override
    public void delete(Integer id) {
        if (!doctorsRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor not found.");
        }
        doctorsRepository.deleteById(id);
    }

    @Override
    public List<DoctorDTO> list() {
        return doctorsRepository.findAll().stream()
                .map(d -> {
                    DoctorDTO dto = modelMapper.map(d, DoctorDTO.class);
                    if (d.getDepartment() != null) {
                        dto.setDepartment_id(d.getDepartment().getId());
                    }
                    dto.setDoctor_id(d.getId()); // Đã có để gán doctor_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorDTO> search(DoctorDTO doctorDTO) {
        List<Doctors> doctors = new ArrayList<>();
        if (doctorDTO.getDoctor_email() != null) {
            doctorsRepository.findByDoctorEmail(doctorDTO.getDoctor_email()).ifPresent(doctors::add);
        } else if (doctorDTO.getDoctor_username() != null) {
            doctorsRepository.findByDoctorUsername(doctorDTO.getDoctor_username()).ifPresent(doctors::add);
        } else {
            doctors = doctorsRepository.findAll();
        }
        return doctors.stream()
                .map(d -> {
                    DoctorDTO dto = modelMapper.map(d, DoctorDTO.class);
                    if (d.getDepartment() != null) {
                        dto.setDepartment_id(d.getDepartment().getId());
                    }
                    dto.setDoctor_id(d.getId()); // Đã có để gán doctor_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> login(LoginRequestDTO loginRequest) {
        Doctors doctor = doctorsRepository.findByDoctorUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));
        if (!loginRequest.getPassword().equals(doctor.getDoctor_password())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("doctor_id", doctor.getId()); // Đã gán doctor_id
        response.put("doctor_name", doctor.getDoctor_name());
        response.put("doctor_description", doctor.getDoctor_description());
        response.put("department_id", doctor.getDepartment() != null ? doctor.getDepartment().getId() : null);
        response.put("doctor_username", doctor.getDoctor_username());
        response.put("summary", doctor.getSummary());
        return response;
    }

    @Override
    public DoctorDTO getById(Integer id) {
        Doctors doctor = doctorsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        DoctorDTO dto = modelMapper.map(doctor, DoctorDTO.class);
        if (doctor.getDepartment() != null) {
            dto.setDepartment_id(doctor.getDepartment().getId());
        }
        dto.setDoctor_id(doctor.getId()); // Đã có để gán doctor_id
        return dto;
    }

    @Override
    public List<DoctorDTO> searchByKeyword(String keyword) {
        return doctorsRepository.searchByKeyword(keyword).stream()
                .map(d -> {
                    DoctorDTO dto = modelMapper.map(d, DoctorDTO.class);
                    if (d.getDepartment() != null) {
                        dto.setDepartment_id(d.getDepartment().getId());
                    }
                    dto.setDoctor_id(d.getId()); // Đã thêm để gán doctor_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void insertAll(List<DoctorDTO> doctorDTOs) {
        List<Doctors> doctors = doctorDTOs.stream()
                .map(dto -> {
                    Doctors d = modelMapper.map(dto, Doctors.class);
                    if (dto.getDepartment_id() != null) {
                        Departments department = departmentsRepository.findById(dto.getDepartment_id())
                                .orElseThrow(() -> new IllegalArgumentException("Department not found."));
                        d.setDepartment(department);
                    }
                    return d;
                })
                .collect(Collectors.toList());
        List<Doctors> savedDoctors = doctorsRepository.saveAll(doctors);
        // Cập nhật DTO với doctor_id sau khi lưu
        for (int i = 0; i < doctorDTOs.size(); i++) {
            doctorDTOs.get(i).setDoctor_id(savedDoctors.get(i).getId()); // Đã thêm để gán doctor_id
        }
    }
}