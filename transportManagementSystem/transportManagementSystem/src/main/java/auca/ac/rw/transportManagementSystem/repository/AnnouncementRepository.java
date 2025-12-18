package auca.ac.rw.transportManagementSystem.repository;

import auca.ac.rw.transportManagementSystem.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByTargetAudience(String targetAudience);
}
