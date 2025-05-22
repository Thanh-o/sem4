package com.example.usersevice.service;

import com.example.usersevice.entity.Doctors;
import com.example.usersevice.entity.Patients;
import com.example.usersevice.entity.Staffs;
import com.example.usersevice.repository.DoctorsRepository;
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
    @Autowired
    private StaffsRepository staffsRepository;
    @Autowired
    private DoctorsRepository doctorsRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ưu tiên staff
        Staffs staff = staffsRepository.findByStaffUsername(username).orElse(null);
        if (staff != null) {
            String role = switch (staff.getStaff_type()) {
                case "admin" -> "ROLE_ADMIN";
                case "staff" -> "ROLE_STAFF";
                default -> "ROLE_UNKNOWN";
            };

            return new User(
                    staff.getStaff_username(),
                    staff.getStaff_password(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        // Tiếp theo là patient
        Patients patient = patientsRepository.findByPatientUsername(username).orElse(null);
        if (patient != null) {
            return new User(
                    patient.getPatient_username(),
                    patient.getPatient_password(),
                    List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
            );
        }

        // Cuối cùng là doctor
        Doctors doctor = doctorsRepository.findByDoctorUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
                doctor.getDoctor_username(),
                doctor.getDoctor_password(),
                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
        );
    }


}
