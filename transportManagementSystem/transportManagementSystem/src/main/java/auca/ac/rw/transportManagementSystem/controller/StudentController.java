package auca.ac.rw.transportManagementSystem.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.ac.rw.transportManagementSystem.model.Student;
import auca.ac.rw.transportManagementSystem.model.StudentStatus;
import auca.ac.rw.transportManagementSystem.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveStudent(@RequestBody Student student) {
        String response = studentService.saveStudent(student);
        if ("Student saved successfully".equals(response)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/assignParent")
    public ResponseEntity<String> assignParent(@RequestParam int studentId, 
                                                @RequestParam UUID parentId) {
        String response = studentService.assignParent(studentId, parentId);
        if ("Parent assigned successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/assignBus")
    public ResponseEntity<String> assignBus(@RequestParam int studentId, 
                                            @RequestParam int busId) {
        String response = studentService.assignBus(studentId, busId);
        if ("Bus assigned successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<String> updateStudentStatus(@RequestParam int studentId, @RequestParam StudentStatus status) {
        String response = studentService.updateStudentStatus(studentId, status);
        if ("Student status updated successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/driver/updateStatus")
    public ResponseEntity<String> updateStudentStatusByDriver(
            @RequestParam int studentId, 
            @RequestParam StudentStatus status) {
        String response = studentService.updateStudentStatus(studentId, status);
        if ("Student status updated successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            return ResponseEntity.ok(student.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/class/{className}")
    public ResponseEntity<List<Student>> getStudentsByClass(@PathVariable String className) {
        List<Student> students = studentService.getStudentsByClass(className);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Student>> getStudentsByStatus(@PathVariable StudentStatus status) {
        List<Student> students = studentService.getStudentsByStatus(status);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Student>> getStudentsByParent(@PathVariable UUID parentId) {
        List<Student> students = studentService.getStudentsByParent(parentId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<Student>> getStudentsByBus(@PathVariable int busId) {
        List<Student> students = studentService.getStudentsByBus(busId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/pickup/{pickUpPoint}")
    public ResponseEntity<List<Student>> getStudentsByPickUpPoint(@PathVariable String pickUpPoint) {
        List<Student> students = studentService.getStudentsByPickUpPoint(pickUpPoint);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/dropoff/{dropOffPoint}")
    public ResponseEntity<List<Student>> getStudentsByDropOffPoint(@PathVariable String dropOffPoint) {
        List<Student> students = studentService.getStudentsByDropOffPoint(dropOffPoint);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String name) {
        List<Student> students = studentService.searchStudentsByName(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/driver/{driverEmail}")
    public ResponseEntity<?> getStudentsByDriverEmail(@PathVariable String driverEmail) {
        try {
            List<Student> students = studentService.getStudentsByDriverEmail(driverEmail);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching students: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateStudent(@PathVariable int id, 
                                                 @RequestBody Student student) {
        String response = studentService.updateStudent(id, student);
        if ("Student updated successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {
        String response = studentService.deleteStudent(id);
        if ("Student deleted successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/statistics/{studentEmail}")
    public ResponseEntity<?> getStudentStatistics(@PathVariable String studentEmail) {
        try {
            // Decode URL-encoded email (e.g., %40 becomes @)
            String decodedEmail = URLDecoder.decode(studentEmail, StandardCharsets.UTF_8);
            System.out.println("DEBUG Controller: Original email param: " + studentEmail);
            System.out.println("DEBUG Controller: Decoded email: " + decodedEmail);
            
            Map<String, Object> stats = studentService.getStudentStatistics(decodedEmail);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: IllegalArgumentException in getStudentStatistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR in getStudentStatistics controller: " + e.getMessage());
            e.printStackTrace();
            // Return empty stats instead of error to prevent 500
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("announcementCount", 0);
            emptyStats.put("scheduleCount", 0);
            return ResponseEntity.ok(emptyStats);
        }
    }
}