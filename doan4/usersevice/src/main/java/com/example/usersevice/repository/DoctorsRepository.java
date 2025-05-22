package com.example.usersevice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.usersevice.entity.Doctors;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorsRepository extends JpaRepository<Doctors, Integer> {

    @Query("SELECT d FROM Doctors d WHERE d.doctor_email = :email")
    Optional<Doctors> findByDoctorEmail(String email);

    @Query("SELECT d FROM Doctors d WHERE d.doctor_username = :username")
    Optional<Doctors> findByDoctorUsername(String username);

    @Query("SELECT d FROM Doctors d WHERE d.doctor_name LIKE %:keyword% OR d.doctor_email LIKE %:keyword%")
    List<Doctors> searchByKeyword(String keyword);
}