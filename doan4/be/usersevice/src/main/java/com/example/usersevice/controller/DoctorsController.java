package com.example.usersevice.controller;

import com.example.usersevice.dto.DoctorDTO;
import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.service.DoctorsService;
import com.example.usersevice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorsController {

    @Autowired
    private DoctorsService doctorsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Map<String, Object> doctor = doctorsService.login(loginRequest);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("success", false));
        }
        String username = (String) doctor.get("patient_name");
        String token = jwtUtil.generateToken(loginRequest.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("doctor", doctor);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody DoctorDTO doctorDTO) {
        try {
            doctorsService.insert(doctorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody DoctorDTO doctorDTO) {
        try {
            doctorsService.update(doctorDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            doctorsService.delete(id);
            return ResponseEntity.ok("success");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<DoctorDTO>> list() {
        return ResponseEntity.ok(doctorsService.list());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> search(@RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.ok(doctorsService.search(doctorDTO));
    }
}
