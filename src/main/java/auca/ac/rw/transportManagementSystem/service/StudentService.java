package auca.ac.rw.transportManagementSystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.ac.rw.transportManagementSystem.model.Bus;
import auca.ac.rw.transportManagementSystem.model.Student;
import auca.ac.rw.transportManagementSystem.model.StudentStatus;
import auca.ac.rw.transportManagementSystem.model.User;
import auca.ac.rw.transportManagementSystem.repository.BusRepository;
import auca.ac.rw.transportManagementSystem.repository.StudentRepository;
import auca.ac.rw.transportManagementSystem.repository.UserRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusRepository busRepository;

    public String saveStudent(Student student) {
        if (student == null) {
            return "Student cannot be null";
        }

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return "Student name is required";
        }

        if (student.getClassName() == null || student.getClassName().trim().isEmpty()) {
            return "Class name is required";
        }

        try {
            studentRepository.save(student);
            return "Student saved successfully";
        } catch (Exception e) {
            return "Failed to save student: " + e.getMessage();
        }
    }

    public String saveStudentWithParentAndBus(Student student, UUID parentId, Integer busId) {
        if (student == null) {
            return "Student cannot be null";
        }

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return "Student name is required";
        }

        if (student.getClassName() == null || student.getClassName().trim().isEmpty()) {
            return "Class name is required";
        }

        if (parentId != null) {
            Optional<User> parentOptional = userRepository.findById(parentId);
            if (!parentOptional.isPresent()) {
                return "Parent not found";
            }
            student.setParent(parentOptional.get());
        }

        if (busId != null) {
            Optional<Bus> busOptional = busRepository.findById(busId);
            if (!busOptional.isPresent()) {
                return "Bus not found";
            }
            student.setBus(busOptional.get());
        }

        try {
            studentRepository.save(student);
            return "Student saved successfully";
        } catch (Exception e) {
            return "Failed to save student: " + e.getMessage();
        }
    }

    public String assignParent(int studentId, UUID parentId) {
        if (parentId == null) {
            return "Parent ID is required";
        }

        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (!studentOptional.isPresent()) {
            return "Student not found";
        }

        Optional<User> parentOptional = userRepository.findById(parentId);
        if (!parentOptional.isPresent()) {
            return "Parent not found";
        }

        Student student = studentOptional.get();
        student.setParent(parentOptional.get());

        try {
            studentRepository.save(student);
            return "Parent assigned successfully";
        } catch (Exception e) {
            return "Failed to assign parent: " + e.getMessage();
        }
    }

    public String assignBus(int studentId, int busId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (!studentOptional.isPresent()) {
            return "Student not found";
        }

        Optional<Bus> busOptional = busRepository.findById(busId);
        if (!busOptional.isPresent()) {
            return "Bus not found";
        }

        Student student = studentOptional.get();
        student.setBus(busOptional.get());

        try {
            studentRepository.save(student);
            return "Bus assigned successfully";
        } catch (Exception e) {
            return "Failed to assign bus: " + e.getMessage();
        }
    }

    public String updateStudentStatus(int studentId, StudentStatus status) {
        if (status == null) {
            return "Status cannot be null";
        }

        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (!studentOptional.isPresent()) {
            return "Student not found";
        }

        Student student = studentOptional.get();
        student.setStatus(status);

        try {
            studentRepository.save(student);
            return "Student status updated successfully";
        } catch (Exception e) {
            return "Failed to update status: " + e.getMessage();
        }
    }

    public Optional<Student> getStudentById(int id) {
        return studentRepository.findById(id);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }

    public List<Student> getStudentsByStatus(StudentStatus status) {
        return studentRepository.findByStatus(status);
    }

    public List<Student> getStudentsByParent(UUID parentId) {
        return studentRepository.findByParentUserId(parentId);
    }

    public List<Student> getStudentsByBus(int busId) {
        return studentRepository.findByBusId(busId);
    }

    public List<Student> getStudentsByPickUpPoint(String pickUpPoint) {
        return studentRepository.findByPickUpPoint(pickUpPoint);
    }

    public List<Student> getStudentsByDropOffPoint(String dropOffPoint) {
        return studentRepository.findByDropOffPoint(dropOffPoint);
    }

    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public String updateStudent(int id, Student updatedStudent) {
        Optional<Student> existingStudentOptional = studentRepository.findById(id);
        if (!existingStudentOptional.isPresent()) {
            return "Student not found";
        }

        Student student = existingStudentOptional.get();

        if (updatedStudent.getName() != null && !updatedStudent.getName().trim().isEmpty()) {
            student.setName(updatedStudent.getName());
        }

        if (updatedStudent.getClassName() != null && !updatedStudent.getClassName().trim().isEmpty()) {
            student.setClassName(updatedStudent.getClassName());
        }

        if (updatedStudent.getPickUpPoint() != null) {
            student.setPickUpPoint(updatedStudent.getPickUpPoint());
        }

        if (updatedStudent.getDropOffPoint() != null) {
            student.setDropOffPoint(updatedStudent.getDropOffPoint());
        }

        if (updatedStudent.getStatus() != null) {
            student.setStatus(updatedStudent.getStatus());
        }

        try {
            studentRepository.save(student);
            return "Student updated successfully";
        } catch (Exception e) {
            return "Failed to update student: " + e.getMessage();
        }
    }

    public String deleteStudent(int id) {
        Optional<Student> studentOptional = studentRepository.findById(id);
        if (!studentOptional.isPresent()) {
            return "Student not found";
        }

        try {
            studentRepository.deleteById(id);
            return "Student deleted successfully";
        } catch (Exception e) {
            return "Failed to delete student: " + e.getMessage();
        }
    }
}