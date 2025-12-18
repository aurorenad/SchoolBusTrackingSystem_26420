package auca.ac.rw.transportManagementSystem.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auca.ac.rw.transportManagementSystem.model.Announcement;
import auca.ac.rw.transportManagementSystem.model.Bus;
import auca.ac.rw.transportManagementSystem.model.Driver;
import auca.ac.rw.transportManagementSystem.model.Location;
import auca.ac.rw.transportManagementSystem.model.Schedule;
import auca.ac.rw.transportManagementSystem.model.Student;
import auca.ac.rw.transportManagementSystem.model.StudentStatus;
import auca.ac.rw.transportManagementSystem.model.OtpPurpose;
import auca.ac.rw.transportManagementSystem.model.Role;
import auca.ac.rw.transportManagementSystem.model.User;
import auca.ac.rw.transportManagementSystem.repository.BusRepository;
import auca.ac.rw.transportManagementSystem.repository.DriverRepository;
import auca.ac.rw.transportManagementSystem.repository.LocationRepository;
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

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private ScheduleService scheduleService;

    @Transactional
    public String saveStudent(Student student) {
        if (student == null) {
            return "Student cannot be null";
        }

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return "Student name is required";
        }

        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            return "Student email is required";
        }

        if (student.getClassName() == null || student.getClassName().trim().isEmpty()) {
            return "Class name is required";
        }

        // Default status if missing
        if (student.getStatus() == null) {
            student.setStatus(StudentStatus.ABSENT);
        }

        // Require a location (village) for student registration
        if (student.getLocation() == null || student.getLocation().getCode() == null
                || student.getLocation().getCode().trim().isEmpty()) {
            return "Student location is required";
        }

        Optional<Location> locationOpt = locationRepository.findByCode(student.getLocation().getCode());
        if (!locationOpt.isPresent()) {
            return "Location not found";
        }
        student.setLocation(locationOpt.get());

        try {
            String normalizedEmail = student.getEmail().trim().toLowerCase();

            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                return "User with this email already exists";
            }

            // Create login account for student
            User user = new User();
            user.setFullNames(student.getName());
            user.setEmail(normalizedEmail);
            user.setRole(Role.STUDENT);
            user.setLocation(student.getLocation());

            // Temporary random password; user will set a real one via OTP
            String tempPassword = UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);

            // Store normalized email on the student record
            student.setEmail(normalizedEmail);
            studentRepository.save(student);

            // Send OTP for password setup (valid 5 minutes)
            otpService.sendPasswordOtp(normalizedEmail, OtpPurpose.PASSWORD_SETUP);
            return "Student saved successfully";
        } catch (Exception e) {
            // Avoid swallowing runtime exceptions inside @Transactional (prevents UnexpectedRollbackException 500s)
            throw new IllegalArgumentException("Failed to save student: " + e.getMessage(), e);
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

    public List<Student> getStudentsByDriverEmail(String driverEmail) {
        if (driverEmail == null || driverEmail.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            // Find driver by email
            Optional<Driver> driverOpt = driverRepository.findByEmailIgnoreCase(driverEmail.trim().toLowerCase());
            if (driverOpt.isEmpty()) {
                return List.of();
            }
            
            Driver driver = driverOpt.get();
            
            // Find bus assigned to this driver
            Optional<Bus> busOpt = busRepository.findByDriverDriverId(driver.getDriverId());
            if (busOpt.isEmpty()) {
                return List.of();
            }
            
            Bus bus = busOpt.get();
            
            // Find all students assigned to this bus
            return studentRepository.findByBusId(bus.getId());
        } catch (Exception e) {
            return List.of();
        }
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

        if (updatedStudent.getLocation() != null && updatedStudent.getLocation().getCode() != null
                && !updatedStudent.getLocation().getCode().trim().isEmpty()) {
            Optional<Location> locationOpt = locationRepository.findByCode(updatedStudent.getLocation().getCode());
            if (!locationOpt.isPresent()) {
                return "Location not found";
            }
            student.setLocation(locationOpt.get());
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

    @Transactional(readOnly = true)
    public Map<String, Object> getStudentStatistics(String studentEmail) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("announcementCount", 0);
        stats.put("scheduleCount", 0);

        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            return stats;
        }

        // Get announcement count for students
        if (announcementService != null) {
            try {
                List<Announcement> studentAnnouncements = announcementService.getAnnouncementsForStudentsAndAll();
                int announcementCount = studentAnnouncements != null ? studentAnnouncements.size() : 0;
                System.out.println("DEBUG: Announcement count: " + announcementCount);
                stats.put("announcementCount", announcementCount);
            } catch (Exception e) {
                System.err.println("ERROR getting announcements: " + e.getMessage());
                e.printStackTrace();
                stats.put("announcementCount", 0);
            }
        } else {
            System.err.println("WARNING: announcementService is null");
            stats.put("announcementCount", 0);
        }
        
        // Get schedule count - count ALL schedules (like the schedule page shows)
        if (scheduleService != null) {
            try {
                List<Schedule> allSchedules = scheduleService.getAllSchedules();
                int scheduleCount = allSchedules != null ? allSchedules.size() : 0;
                System.out.println("DEBUG: Schedule count: " + scheduleCount);
                stats.put("scheduleCount", scheduleCount);
            } catch (Exception e) {
                System.err.println("ERROR getting schedules: " + e.getMessage());
                e.printStackTrace();
                stats.put("scheduleCount", 0);
            }
        } else {
            System.err.println("WARNING: scheduleService is null");
            stats.put("scheduleCount", 0);
        }

        return stats;
    }
}