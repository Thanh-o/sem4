package com.example.usersevice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.usersevice.entity.Patients;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientsRepository extends JpaRepository<Patients, Integer> {

    @Query("SELECT p FROM Patients p WHERE p.patient_email = :email")
    Optional<Patients> findByPatientEmail(String email);

    @Query("SELECT p FROM Patients p WHERE p.patient_username = :username")
    Optional<Patients> findByPatientUsername(String username);

    @Query("SELECT p FROM Patients p WHERE p.patient_name LIKE %:keyword% OR p.patient_email LIKE %:keyword% OR p.patient_phone LIKE %:keyword%")
    List<Patients> searchByKeyword(String keyword);

    Optional<Patients> findById(Long id);

}