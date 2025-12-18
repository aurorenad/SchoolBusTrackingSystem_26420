package auca.ac.rw.transportManagementSystem.service;

import auca.ac.rw.transportManagementSystem.model.Announcement;
import auca.ac.rw.transportManagementSystem.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    public String saveAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        try {
            announcementRepository.save(announcement);
            return "Announcement saved successfully";
        } catch (Exception e) {
            return "Error saving announcement: " + e.getMessage();
        }
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public List<Announcement> getAnnouncementsForDrivers() {
        return announcementRepository.findByTargetAudience("DRIVER");
    }

    public List<Announcement> getAnnouncementsForAll() {
        return announcementRepository.findByTargetAudience("ALL");
    }

    public List<Announcement> getAnnouncementsForDriversAndAll() {
        List<Announcement> driverAnnouncements = announcementRepository.findByTargetAudience("DRIVER");
        List<Announcement> allAnnouncements = announcementRepository.findByTargetAudience("ALL");
        List<Announcement> combined = new java.util.ArrayList<>(driverAnnouncements);
        combined.addAll(allAnnouncements);
        return combined;
    }

    public List<Announcement> getAnnouncementsForStudents() {
        return announcementRepository.findByTargetAudience("STUDENT");
    }

    public List<Announcement> getAnnouncementsForStudentsAndAll() {
        List<Announcement> studentAnnouncements = announcementRepository.findByTargetAudience("STUDENT");
        List<Announcement> allAnnouncements = announcementRepository.findByTargetAudience("ALL");
        List<Announcement> combined = new java.util.ArrayList<>(studentAnnouncements);
        combined.addAll(allAnnouncements);
        return combined;
    }
}
