package com.example.usersevice.controller;



import com.example.usersevice.dto.LoginRequestDTO;
import com.example.usersevice.dto.StaffDTO;
import com.example.usersevice.security.JwtUtil;
import com.example.usersevice.service.StaffsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/staffs")
public class StaffsController {

    @Autowired
    private StaffsService staffsService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody StaffDTO staffDTO) {
        try {
            staffsService.insert(staffDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody StaffDTO staffDTO) {
        try {
            staffsService.update(staffDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            staffsService.delete(id);
            return ResponseEntity.ok("success");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<StaffDTO>> list() {
        return ResponseEntity.ok(staffsService.list());
    }

    @GetMapping("/search")
    public ResponseEntity<List<StaffDTO>> search(@RequestBody StaffDTO staffDTO) {
        return ResponseEntity.ok(staffsService.search(staffDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequestDTO loginRequest) {
        try {
            StaffDTO staffDTO = staffsService.loginAdmin(loginRequest);
            String token = jwtUtil.generateToken(loginRequest.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("staff", staffDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/loginStaff")
    public ResponseEntity<?> loginStaff(@RequestBody LoginRequestDTO loginRequest) {
        try {
            StaffDTO staffDTO = staffsService.loginStaff(loginRequest);
            String token = jwtUtil.generateToken(loginRequest.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("staff", staffDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<StaffDTO> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(staffsService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search-new")
    public ResponseEntity<List<StaffDTO>> searchByKeyword(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(staffsService.searchByKeyword(keyword));
    }

    @PostMapping("/insertAll")
    public ResponseEntity<Void> insertAll(@RequestBody List<StaffDTO> staffDTOs) {
        try {
            staffsService.insertAll(staffDTOs);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Chỉ admin mới được vào");
    }
}
