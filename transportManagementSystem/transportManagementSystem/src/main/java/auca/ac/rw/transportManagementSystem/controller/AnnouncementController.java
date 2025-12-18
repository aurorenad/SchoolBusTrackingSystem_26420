package auca.ac.rw.transportManagementSystem.controller;

import auca.ac.rw.transportManagementSystem.model.Announcement;
import auca.ac.rw.transportManagementSystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping("/save")
    public ResponseEntity<String> saveAnnouncement(@RequestBody Announcement announcement) {
        return ResponseEntity.ok(announcementService.saveAnnouncement(announcement));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Announcement>> getDriverAnnouncements() {
        return ResponseEntity.ok(announcementService.getAnnouncementsForDriversAndAll());
    }

    @GetMapping("/students")
    public ResponseEntity<List<Announcement>> getStudentAnnouncements() {
        return ResponseEntity.ok(announcementService.getAnnouncementsForStudentsAndAll());
    }
}
