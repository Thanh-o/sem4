package com.example.usersevice.repository;


import com.example.usersevice.entity.Medicalrecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface MedicalrecordsRepository extends JpaRepository<Medicalrecords, Integer> {
    List<Medicalrecords> findByPatientId(Integer patientId);
    List<Medicalrecords> findByDoctorId(Integer doctorId);
}
