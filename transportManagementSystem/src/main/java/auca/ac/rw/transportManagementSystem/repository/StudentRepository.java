package auca.ac.rw.transportManagementSystem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import auca.ac.rw.transportManagementSystem.model.Student;
import auca.ac.rw.transportManagementSystem.model.StudentStatus;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    
    List<Student> findByClassName(String className);
    
    List<Student> findByStatus(StudentStatus status);
    
    List<Student> findByParentUserId(UUID parentId);
    
    List<Student> findByBusId(int busId);
    
    List<Student> findByPickUpPoint(String pickUpPoint);
    
    List<Student> findByDropOffPoint(String dropOffPoint);
    
    List<Student> findByNameContainingIgnoreCase(String name);
}