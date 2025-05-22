package com.example.usersevice.controller;



import com.example.usersevice.dto.ChangePasswordRequestDTO;
import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.dto.PatientDTO;
import com.example.usersevice.dto.RegisterRequestDTO;
import com.example.usersevice.security.JwtUtil;
import com.example.usersevice.service.PatientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientsController {

    @Autowired
    private PatientsService patientsService;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody PatientDTO patientDTO) {
        patientsService.insert(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        patientsService.delete(id);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/list")
    public ResponseEntity<List<PatientDTO>> list() {
        return ResponseEntity.ok(patientsService.list());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> search(@ModelAttribute PatientDTO patientDTO) {
        try {
            return ResponseEntity.ok(patientsService.search(patientDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Map<String, Object> patient = patientsService.login(loginRequest);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("success", false));
        }
        String username = (String) patient.get("patient_name");
        String token = jwtUtil.generateToken(loginRequest.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("patient", patient);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(patientsService.googleLogin(request));
    }

    @PostMapping("/facebook-login")
    public ResponseEntity<?> facebookLogin(@RequestBody Map<String, String> request) {
        try {
            return ResponseEntity.ok(patientsService.facebookLogin(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/insertAll")
    public ResponseEntity<Void> insertAll(@RequestBody List<PatientDTO> patientDTOs) {
        patientsService.insertAll(patientDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(patientsService.register(registerRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        try {
            patientsService.changePassword(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody PatientDTO patientDTO) {
        patientsService.update(patientDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("patient_image") MultipartFile image,
            @RequestParam("patient_id") Integer patientId) {
        try {
            return ResponseEntity.ok(patientsService.uploadImage(image, patientId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search-new")
    public ResponseEntity<List<PatientDTO>> searchByKeyword(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(patientsService.searchByKeyword(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(patientsService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/admin-only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Chỉ admin mới được vào");
    }

}
