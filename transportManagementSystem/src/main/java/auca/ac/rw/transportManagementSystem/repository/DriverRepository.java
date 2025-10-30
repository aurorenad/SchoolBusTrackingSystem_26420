package auca.ac.rw.transportManagementSystem.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import auca.ac.rw.transportManagementSystem.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
    
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    List<Driver> findByExperienceYearsGreaterThanEqual(int years);
    
    List<Driver> findByNameContainingIgnoreCase(String name);
}