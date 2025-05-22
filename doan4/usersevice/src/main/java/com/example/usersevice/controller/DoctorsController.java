package com.example.usersevice.controller;



import com.example.usersevice.dto.DoctorDTO;
import com.example.usersevice.service.DoctorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorsController {

    @Autowired
    private DoctorsService doctorsService;

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
