package com.example.usersevice.controller;



import com.example.usersevice.dto.MedicalrecordDTO;
import com.example.usersevice.service.MedicalrecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/medicalrecords")
public class MedicalrecordsController {

    @Autowired
    private MedicalrecordsService medicalrecordsService;

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody MedicalrecordDTO medicalrecordDTO) {
        try {
            medicalrecordsService.insert(medicalrecordDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody MedicalrecordDTO medicalrecordDTO) {
        try {
            medicalrecordsService.update(medicalrecordDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            medicalrecordsService.delete(id);
            return ResponseEntity.ok("success");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<MedicalrecordDTO>> list() {
        return ResponseEntity.ok(medicalrecordsService.list());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicalrecordDTO>> search(@RequestBody MedicalrecordDTO medicalrecordDTO) {
        return ResponseEntity.ok(medicalrecordsService.search(medicalrecordDTO));
    }

    @GetMapping("/fields")
    public ResponseEntity<List<String>> getFields() {
        return ResponseEntity.ok(medicalrecordsService.getFields());
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalrecordDTO>> getByDoctorId(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(medicalrecordsService.getByDoctorId(doctorId));
    }

    @PostMapping("/images/upload")
    public ResponseEntity<Map<String, Object>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            return ResponseEntity.ok(medicalrecordsService.uploadImages(files));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
