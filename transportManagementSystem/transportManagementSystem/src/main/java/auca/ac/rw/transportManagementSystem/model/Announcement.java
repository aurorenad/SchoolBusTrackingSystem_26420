package auca.ac.rw.transportManagementSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;
    private String message;
    
    // Target: ALL, DRIVER, STUDENT
    private String targetAudience;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
