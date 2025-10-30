package auca.ac.rw.transportManagementSystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.ac.rw.transportManagementSystem.model.Elocation;
import auca.ac.rw.transportManagementSystem.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    
    Optional<Location> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Location> findByType(Elocation type);
    
    List<Location> findByParent(Location parent);
    
    List<Location> findByParentIsNull();
    
    @Query("SELECT l FROM Location l WHERE l.parent.code = :parentCode")
    List<Location> findByParentCode(@Param("parentCode") String parentCode);
    
    @Query("SELECT l FROM Location l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Location> searchByName(@Param("name") String name);
    
    @Query("SELECT COUNT(l) FROM Location l WHERE l.parent.locationId = :parentId")
    long countChildrenByParentId(@Param("parentId") UUID parentId);
}