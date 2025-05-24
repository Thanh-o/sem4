package com.example.usersevice.repository;




import com.example.usersevice.entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DepartmentsRepository extends JpaRepository<Departments, Integer> {
}
