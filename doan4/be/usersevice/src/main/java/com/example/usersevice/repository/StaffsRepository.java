package com.example.usersevice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.usersevice.entity.Staffs;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffsRepository extends JpaRepository<Staffs, Integer> {

    @Query("SELECT s FROM Staffs s WHERE s.staff_email = :email")
    Optional<Staffs> findByStaffEmail(String email);

    @Query("SELECT s FROM Staffs s WHERE s.staff_username = :username")
    Optional<Staffs> findByStaffUsername(String username);

    @Query("SELECT s FROM Staffs s WHERE s.staff_name LIKE %:keyword% OR s.staff_email LIKE %:keyword%")
    List<Staffs> searchByKeyword(String keyword);
}