package com.example.usersevice.service;

import com.example.usersevice.dto.MedicalrecordDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MedicalrecordsService {

    void insert(MedicalrecordDTO medicalrecordDTO);

    void update(MedicalrecordDTO medicalrecordDTO);

    void delete(Integer id);

    List<MedicalrecordDTO> list();

    List<MedicalrecordDTO> search(MedicalrecordDTO medicalrecordDTO);

    List<String> getFields();

    List<MedicalrecordDTO> getByDoctorId(Integer doctorId);

    Map<String, Object> uploadImages(MultipartFile[] files) throws IOException;
}