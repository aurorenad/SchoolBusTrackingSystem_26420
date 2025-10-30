package auca.ac.rw.transportManagementSystem.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        if (response.equals("Student saved successfully")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/assignParent")
    public ResponseEntity<String> assignParent(@RequestParam int studentId, 
                                                @RequestParam UUID parentId) {
        String response = studentService.assignParent(studentId, parentId);
        if (response.equals("Parent assigned successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/assignBus")
    public ResponseEntity<String> assignBus(@RequestParam int studentId, 
                                            @RequestParam int busId) {
        String response = studentService.assignBus(studentId, busId);
        if (response.equals("Bus assigned successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<String> updateStudentStatus(@RequestParam int studentId, @RequestParam StudentStatus status) {
        String response = studentService.updateStudentStatus(studentId, status);
        if (response.equals("Student status updated successfully")) {
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

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateStudent(@PathVariable int id, 
                                                 @RequestBody Student student) {
        String response = studentService.updateStudent(id, student);
        if (response.equals("Student updated successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {
        String response = studentService.deleteStudent(id);
        if (response.equals("Student deleted successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}