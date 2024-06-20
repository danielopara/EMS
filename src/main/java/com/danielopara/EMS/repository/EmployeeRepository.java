package com.danielopara.EMS.repository;

import com.danielopara.EMS.entity.Employee;
import com.danielopara.EMS.entity.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}

