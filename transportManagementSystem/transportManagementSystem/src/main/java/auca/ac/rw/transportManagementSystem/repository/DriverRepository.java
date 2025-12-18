package auca.ac.rw.transportManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import auca.ac.rw.transportManagementSystem.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
    
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    
    Optional<Driver> findByLicenseNumberIgnoreCase(String licenseNumber);

    Optional<Driver> findByEmail(String email);

    Optional<Driver> findByEmailIgnoreCase(String email);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    boolean existsByLicenseNumberIgnoreCase(String licenseNumber);

    boolean existsByEmail(String email);
    
    boolean existsByEmailIgnoreCase(String email);
    
    List<Driver> findByExperienceYearsGreaterThanEqual(int years);
    
    List<Driver> findByNameContainingIgnoreCase(String name);
}