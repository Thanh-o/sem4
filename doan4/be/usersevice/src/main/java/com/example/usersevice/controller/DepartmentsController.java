package com.example.usersevice.controller;

import com.example.usersevice.dto.DepartmentDTO;
import com.example.usersevice.dto.DoctorDTO;
import com.example.usersevice.service.DepartmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentsController {

    @Autowired
    private DepartmentsService departmentsService;

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody DepartmentDTO departmentDTO) {
        try {
            departmentsService.insert(departmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody DepartmentDTO departmentDTO) {
        try {
            departmentsService.update(departmentDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            departmentsService.delete(id);
            return ResponseEntity.ok("success");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<DepartmentDTO>> list() {
        return ResponseEntity.ok(departmentsService.list());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DepartmentDTO>> search(@RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(departmentsService.search(departmentDTO));
    }

    @GetMapping("/{id}/doctors")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByDepartmentId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(departmentsService.getDoctorsByDepartmentId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/insertAll")
    public ResponseEntity<Void> insertAll(@RequestBody List<DepartmentDTO> departmentDTOs) {
        try {
            departmentsService.insertAll(departmentDTOs);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}