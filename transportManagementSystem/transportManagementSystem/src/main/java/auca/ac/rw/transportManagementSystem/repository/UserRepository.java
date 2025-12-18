package auca.ac.rw.transportManagementSystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.ac.rw.transportManagementSystem.model.Gender;
import auca.ac.rw.transportManagementSystem.model.Role;
import auca.ac.rw.transportManagementSystem.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);
    
    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);
    
    List<User> findByRole(Role role);
    
    Page<User> findByRole(Role role, Pageable pageable);
    
    List<User> findByGender(Gender gender);
    
    Page<User> findByGender(Gender gender, Pageable pageable);
    
    List<User> findByLocationCode(String locationCode);
    
    Page<User> findByLocationCode(String locationCode, Pageable pageable);
    
    List<User> findByFullNamesContainingIgnoreCase(String name);
    
    Page<User> findByFullNamesContainingIgnoreCase(String name, Pageable pageable);
    
    // Find users by province code - traverses location hierarchy
    @Query("SELECT u FROM User u WHERE " +
           "u.location.code = :provinceCode OR " +
           "u.location.parent.code = :provinceCode OR " +
           "u.location.parent.parent.code = :provinceCode OR " +
           "u.location.parent.parent.parent.code = :provinceCode OR " +
           "u.location.parent.parent.parent.parent.code = :provinceCode")
    List<User> findByProvinceCode(@Param("provinceCode") String provinceCode);
    
    @Query("SELECT u FROM User u WHERE " +
           "u.location.code = :provinceCode OR " +
           "u.location.parent.code = :provinceCode OR " +
           "u.location.parent.parent.code = :provinceCode OR " +
           "u.location.parent.parent.parent.code = :provinceCode OR " +
           "u.location.parent.parent.parent.parent.code = :provinceCode")
    Page<User> findByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);
    
    // Find users by province name - traverses location hierarchy
    @Query("SELECT u FROM User u WHERE " +
           "(u.location.type = 'PROVINCE' AND LOWER(u.location.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.type = 'PROVINCE' AND LOWER(u.location.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%')))")
    List<User> findByProvinceName(@Param("provinceName") String provinceName);
    
    @Query("SELECT u FROM User u WHERE " +
           "(u.location.type = 'PROVINCE' AND LOWER(u.location.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.type = 'PROVINCE' AND LOWER(u.location.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%')))")
    Page<User> findByProvinceName(@Param("provinceName") String provinceName, Pageable pageable);
    
    // Check if user exists by province code
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE " +
           "u.location.code = :provinceCode OR " +
           "u.location.parent.code = :provinceCode OR " +
           "u.location.parent.parent.code = :provinceCode OR " +
           "u.location.parent.parent.parent.code = :provinceCode OR " +
           "u.location.parent.parent.parent.parent.code = :provinceCode")
    boolean existsByProvinceCode(@Param("provinceCode") String provinceCode);
    
    // Check if user exists by province name
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE " +
           "(u.location.type = 'PROVINCE' AND LOWER(u.location.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.type = 'PROVINCE' AND LOWER(u.location.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%'))) OR " +
           "(u.location.parent.parent.parent.parent.type = 'PROVINCE' AND LOWER(u.location.parent.parent.parent.parent.name) LIKE LOWER(CONCAT('%', :provinceName, '%')))")
    boolean existsByProvinceName(@Param("provinceName") String provinceName);
}