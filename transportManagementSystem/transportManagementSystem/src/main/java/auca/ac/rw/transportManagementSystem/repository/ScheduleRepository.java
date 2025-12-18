package auca.ac.rw.transportManagementSystem.repository;

import auca.ac.rw.transportManagementSystem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByBus_Id(int busId);
    List<Schedule> findByRoute_RouteId(int routeId);
}
