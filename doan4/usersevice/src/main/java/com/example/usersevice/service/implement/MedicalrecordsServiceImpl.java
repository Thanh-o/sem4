package com.example.usersevice.service.implement;

import com.example.usersevice.dto.MedicalrecordDTO;
import com.example.usersevice.entity.Doctors;
import com.example.usersevice.entity.Medicalrecords;
import com.example.usersevice.entity.Patients;
import com.example.usersevice.repository.DoctorsRepository;
import com.example.usersevice.repository.MedicalrecordsRepository;
import com.example.usersevice.repository.PatientsRepository;
import com.example.usersevice.service.MedicalrecordsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicalrecordsServiceImpl implements MedicalrecordsService {

    @Autowired
    private MedicalrecordsRepository medicalrecordsRepository;

    @Autowired
    private PatientsRepository patientsRepository;

    @Autowired
    private DoctorsRepository doctorsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void insert(MedicalrecordDTO medicalrecordDTO) {
        Medicalrecords medicalrecord = modelMapper.map(medicalrecordDTO, Medicalrecords.class);
        Patients patient = patientsRepository.findById(medicalrecordDTO.getPatient_id())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        medicalrecord.setPatient(patient);
        if (medicalrecordDTO.getDoctor_id() != null) {
            Doctors doctor = doctorsRepository.findById(medicalrecordDTO.getDoctor_id())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
            medicalrecord.setDoctor(doctor);
        }
        medicalrecordsRepository.save(medicalrecord);
    }

    @Override
    public void update(MedicalrecordDTO medicalrecordDTO) {
        Medicalrecords medicalrecord = medicalrecordsRepository.findById(medicalrecordDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found."));
        modelMapper.map(medicalrecordDTO, medicalrecord);
        Patients patient = patientsRepository.findById(medicalrecordDTO.getPatient_id())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        medicalrecord.setPatient(patient);
        if (medicalrecordDTO.getDoctor_id() != null) {
            Doctors doctor = doctorsRepository.findById(medicalrecordDTO.getDoctor_id())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
            medicalrecord.setDoctor(doctor);
        } else {
            medicalrecord.setDoctor(null);
        }
        medicalrecordsRepository.save(medicalrecord);
    }

    @Override
    public void delete(Integer id) {
        if (!medicalrecordsRepository.existsById(id)) {
            throw new IllegalArgumentException("Medical record not found.");
        }
        medicalrecordsRepository.deleteById(id);
    }

    @Override
    public List<MedicalrecordDTO> list() {
        return medicalrecordsRepository.findAll().stream()
                .map(m -> {
                    MedicalrecordDTO dto = modelMapper.map(m, MedicalrecordDTO.class);
                    dto.setId(m.getId()); // Đã thêm để gán id
                    dto.setPatient_id(m.getPatient().getId());
                    if (m.getDoctor() != null) {
                        dto.setDoctor_id(m.getDoctor().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalrecordDTO> search(MedicalrecordDTO medicalrecordDTO) {
        List<Medicalrecords> medicalrecords = new ArrayList<>();
        if (medicalrecordDTO.getPatient_id() != null) {
            medicalrecords = medicalrecordsRepository.findByPatientId(medicalrecordDTO.getPatient_id());
        } else if (medicalrecordDTO.getDoctor_id() != null) {
            medicalrecords = medicalrecordsRepository.findByDoctorId(medicalrecordDTO.getDoctor_id());
        } else {
            medicalrecords = medicalrecordsRepository.findAll();
        }
        return medicalrecords.stream()
                .map(m -> {
                    MedicalrecordDTO dto = modelMapper.map(m, MedicalrecordDTO.class);
                    dto.setId(m.getId()); // Đã thêm để gán id
                    dto.setPatient_id(m.getPatient().getId());
                    if (m.getDoctor() != null) {
                        dto.setDoctor_id(m.getDoctor().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFields() {
        return Arrays.stream(Medicalrecords.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalrecordDTO> getByDoctorId(Integer doctorId) {
        return medicalrecordsRepository.findByDoctorId(doctorId).stream()
                .map(m -> {
                    MedicalrecordDTO dto = modelMapper.map(m, MedicalrecordDTO.class);
                    dto.setId(m.getId()); // Đã thêm để gán id
                    dto.setPatient_id(m.getPatient().getId());
                    if (m.getDoctor() != null) {
                        dto.setDoctor_id(m.getDoctor().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> uploadImages(MultipartFile[] files) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        String uploadDir = "Uploads/images/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            imagePaths.add(filePath.toString().replace("\\", "/"));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("paths", imagePaths);
        return response;
    }
}