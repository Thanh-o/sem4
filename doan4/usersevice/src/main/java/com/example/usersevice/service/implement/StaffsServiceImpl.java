package com.example.usersevice.service.implement;

import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.dto.StaffDTO;
import com.example.usersevice.entity.Staffs;
import com.example.usersevice.repository.StaffsRepository;
import com.example.usersevice.service.StaffsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffsServiceImpl implements StaffsService {

    @Autowired
    private StaffsRepository staffsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void insert(StaffDTO staffDTO) {
        Staffs staff = modelMapper.map(staffDTO, Staffs.class);
        staffsRepository.save(staff);
    }

    @Override
    public void update(StaffDTO staffDTO) {
        Staffs staff = staffsRepository.findById(staffDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found."));
        modelMapper.map(staffDTO, staff);
        staffsRepository.save(staff);
    }

    @Override
    public void delete(Integer id) {
        if (!staffsRepository.existsById(id)) {
            throw new IllegalArgumentException("Staff not found.");
        }
        staffsRepository.deleteById(id);
    }

    @Override
    public List<StaffDTO> list() {
        return staffsRepository.findAll().stream()
                .map(s -> {
                    StaffDTO dto = modelMapper.map(s, StaffDTO.class);
                    dto.setId(s.getId()); // Đã thêm để gán staff_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StaffDTO> search(StaffDTO staffDTO) {
        List<Staffs> staffs = new ArrayList<>();
        if (staffDTO.getStaff_email() != null) {
            staffsRepository.findByStaffEmail(staffDTO.getStaff_email()).ifPresent(staffs::add);
        } else if (staffDTO.getStaff_username() != null) {
            staffsRepository.findByStaffUsername(staffDTO.getStaff_username()).ifPresent(staffs::add);
        } else {
            staffs = staffsRepository.findAll();
        }
        return staffs.stream()
                .map(s -> {
                    StaffDTO dto = modelMapper.map(s, StaffDTO.class);
                    dto.setId(s.getId()); // Đã thêm để gán staff_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public StaffDTO loginAdmin(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        Staffs staff = staffsRepository.findByStaffUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));
        if (!StaffDTO.STAFF_TYPE_ADMIN.equals(staff.getStaff_type())) {
            throw new IllegalArgumentException("Not an admin.");
        }
        StaffDTO dto = modelMapper.map(staff, StaffDTO.class);
        dto.setId(staff.getId());
        return dto;
    }

    @Override
    public StaffDTO loginStaff(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        Staffs staff = staffsRepository.findByStaffUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));
        if (!loginRequest.getPassword().equals(staff.getStaff_password()) ||
                !StaffDTO.STAFF_TYPE_STAFF.equals(staff.getStaff_type())) {
            throw new IllegalArgumentException("Invalid credentials or not a staff.");
        }
        StaffDTO dto = modelMapper.map(staff, StaffDTO.class);
        dto.setId(staff.getId()); // Đã thêm để gán staff_id
        return dto;
    }

    @Override
    public StaffDTO getById(Integer id) {
        Staffs staff = staffsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found."));
        StaffDTO dto = modelMapper.map(staff, StaffDTO.class);
        dto.setId(staff.getId()); // Đã thêm để gán staff_id
        return dto;
    }

    @Override
    public List<StaffDTO> searchByKeyword(String keyword) {
        return staffsRepository.searchByKeyword(keyword).stream()
                .map(s -> {
                    StaffDTO dto = modelMapper.map(s, StaffDTO.class);
                    dto.setId(s.getId()); // Đã thêm để gán staff_id
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void insertAll(List<StaffDTO> staffDTOs) {
        List<Staffs> staffs = staffDTOs.stream()
                .map(dto -> modelMapper.map(dto, Staffs.class))
                .collect(Collectors.toList());
        List<Staffs> savedStaffs = staffsRepository.saveAll(staffs);
        // Cập nhật DTO với staff_id sau khi lưu
        for (int i = 0; i < staffDTOs.size(); i++) {
            staffDTOs.get(i).setId(savedStaffs.get(i).getId()); // Đã thêm để gán staff_id
        }
    }
}