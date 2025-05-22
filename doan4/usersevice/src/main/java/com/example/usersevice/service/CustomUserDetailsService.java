package com.example.usersevice.service;

import com.example.usersevice.entity.Patients;
import com.example.usersevice.entity.Staffs;
import com.example.usersevice.repository.PatientsRepository;
import com.example.usersevice.repository.StaffsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PatientsRepository patientsRepository;
    private StaffsRepository staffsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ưu tiên tìm trong Staffs trước
        Staffs staff = staffsRepository.findByStaffUsername(username).orElse(null);
        if (staff != null) {
            String role = switch (staff.getStaff_type()) {
                case "ADMIN" -> "ROLE_ADMIN";
                case "STAFF" -> "ROLE_STAFF";
                default -> "ROLE_UNKNOWN";
            };

            return new User(
                    staff.getStaff_username(),
                    staff.getStaff_password(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        // Nếu không phải staff thì tìm trong patients
        Patients patient = patientsRepository.findByPatientUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
                patient.getPatient_username(),
                patient.getPatient_password(),
                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
        );
    }

}
