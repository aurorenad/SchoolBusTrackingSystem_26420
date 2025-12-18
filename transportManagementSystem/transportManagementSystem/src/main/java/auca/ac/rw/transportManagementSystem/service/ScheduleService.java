package auca.ac.rw.transportManagementSystem.service;

import auca.ac.rw.transportManagementSystem.model.Bus;
import auca.ac.rw.transportManagementSystem.model.Driver;
import auca.ac.rw.transportManagementSystem.model.Route;
import auca.ac.rw.transportManagementSystem.model.Schedule;
import auca.ac.rw.transportManagementSystem.repository.BusRepository;
import auca.ac.rw.transportManagementSystem.repository.DriverRepository;
import auca.ac.rw.transportManagementSystem.repository.RouteRepository;
import auca.ac.rw.transportManagementSystem.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private DriverRepository driverRepository;

    public Schedule saveSchedule(Schedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }

        if (schedule.getDepartureTime() == null) {
            throw new IllegalArgumentException("Departure time is required");
        }

        if (schedule.getArrivalTime() == null) {
            throw new IllegalArgumentException("Arrival time is required");
        }

        if (schedule.getDayOfWeek() == null || schedule.getDayOfWeek().trim().isEmpty()) {
            throw new IllegalArgumentException("Day of week is required");
        }

        // Resolve associations by ID to avoid accidentally creating new Bus/Route
        if (schedule.getBus() != null) {
            int busId = schedule.getBus().getId();
            if (busId <= 0) {
                throw new IllegalArgumentException("Bus ID is invalid");
            }
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new IllegalArgumentException("Bus not found"));
            schedule.setBus(bus);
        }

        if (schedule.getRoute() != null) {
            int routeId = schedule.getRoute().getRouteId();
            if (routeId <= 0) {
                throw new IllegalArgumentException("Route ID is invalid");
            }
            Route route = routeRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Route not found"));
            schedule.setRoute(route);
        }

        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getSchedulesByBus(int busId) {
        return scheduleRepository.findByBus_Id(busId);
    }

    public List<Schedule> getSchedulesByRoute(int routeId) {
        return scheduleRepository.findByRoute_RouteId(routeId);
    }

    public List<Schedule> getSchedulesByDriverEmail(String driverEmail) {
        if (driverEmail == null || driverEmail.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // Find driver by email
            Optional<Driver> driverOpt = driverRepository.findByEmailIgnoreCase(driverEmail.trim().toLowerCase());
            if (driverOpt.isEmpty()) {
                return new ArrayList<>();
            }
            
            Driver driver = driverOpt.get();
            
            // Find buses assigned to this driver
            Optional<Bus> busOpt = busRepository.findByDriverDriverId(driver.getDriverId());
            if (busOpt.isEmpty()) {
                return new ArrayList<>();
            }
            
            Bus bus = busOpt.get();
            
            // Find schedules for this bus
            return scheduleRepository.findByBus_Id(bus.getId());
        } catch (Exception e) {
            // If any step fails, return empty list
            return new ArrayList<>();
        }
    }

    public Optional<Schedule> getScheduleById(int id) {
        return scheduleRepository.findById(id);
    }

    public Schedule updateSchedule(int id, Schedule updatedSchedule) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (updatedSchedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }

        if (updatedSchedule.getDepartureTime() != null) {
            existing.setDepartureTime(updatedSchedule.getDepartureTime());
        }

        if (updatedSchedule.getArrivalTime() != null) {
            existing.setArrivalTime(updatedSchedule.getArrivalTime());
        }

        if (updatedSchedule.getDayOfWeek() != null && !updatedSchedule.getDayOfWeek().trim().isEmpty()) {
            existing.setDayOfWeek(updatedSchedule.getDayOfWeek());
        }

        // Allow setting/clearing bus
        if (updatedSchedule.getBus() == null) {
            existing.setBus(null);
        } else {
            int busId = updatedSchedule.getBus().getId();
            if (busId <= 0) {
                throw new IllegalArgumentException("Bus ID is invalid");
            }
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new IllegalArgumentException("Bus not found"));
            existing.setBus(bus);
        }

        // Allow setting/clearing route
        if (updatedSchedule.getRoute() == null) {
            existing.setRoute(null);
        } else {
            int routeId = updatedSchedule.getRoute().getRouteId();
            if (routeId <= 0) {
                throw new IllegalArgumentException("Route ID is invalid");
            }
            Route route = routeRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Route not found"));
            existing.setRoute(route);
        }

        return scheduleRepository.save(existing);
    }

    public void deleteSchedule(int id) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        scheduleRepository.delete(existing);
    }
}
