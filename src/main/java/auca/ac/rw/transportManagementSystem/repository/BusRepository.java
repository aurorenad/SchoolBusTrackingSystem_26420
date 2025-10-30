package auca.ac.rw.transportManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import auca.ac.rw.transportManagementSystem.model.Bus;
import auca.ac.rw.transportManagementSystem.model.EStatus;

@Repository
public interface BusRepository extends JpaRepository<Bus, Integer> {
    
    Optional<Bus> findByPlateNumber(String plateNumber);
    
    boolean existsByPlateNumber(String plateNumber);
    
    List<Bus> findByStatus(EStatus status);
    
    List<Bus> findByRouteRouteId(int routeId);
    
    Optional<Bus> findByDriverDriverId(int driverId);
    
    List<Bus> findByCapacityGreaterThanEqual(int capacity);
}