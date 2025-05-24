package com.example.appointmentservices.repository;

import com.example.appointmentservices.entity.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointments, Integer> {
    List<Appointments> findByDoctorId(Integer doctorId);

    @Query("SELECT a FROM Appointments a WHERE " +
            "(:startDate IS NULL OR a.medicalDay >= :startDate) AND " +
            "(:endDate IS NULL OR a.medicalDay <= :endDate) AND " +
            "(:status IS NULL OR a.status = :status)")
    List<Appointments> findByCriteria(Date startDate, Date endDate, String status);

    @Query("SELECT a FROM Appointments a WHERE " +
            "(:startDate IS NULL OR a.medicalDay >= :startDate) AND " +
            "(:endDate IS NULL OR a.medicalDay <= :endDate) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "a.doctorId = :doctorId")
    List<Appointments> findByCriteriaAndDoctor(Date startDate, Date endDate, String status, Integer doctorId);
}