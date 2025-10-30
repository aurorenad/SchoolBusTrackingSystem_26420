package auca.ac.rw.transportManagementSystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.ac.rw.transportManagementSystem.model.Location;
import auca.ac.rw.transportManagementSystem.model.Elocation;
import auca.ac.rw.transportManagementSystem.repository.LocationRepository;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public String saveProvince(Location location) {
        if (location == null) {
            return "Location cannot be null";
        }
        
        if (location.getCode() == null || location.getCode().trim().isEmpty()) {
            return "Location code is required";
        }
        
        if (location.getName() == null || location.getName().trim().isEmpty()) {
            return "Location name is required";
        }
        
        if (!locationRepository.existsByCode(location.getCode())) {
            locationRepository.save(location);
            return "Province saved successfully";
        } else {
            return "Province with that code already exists";
        }
    }

    public String saveChildren(String parentCode, Location location) {
        if (location == null) {
            return "Location cannot be null";
        }
        
        if (location.getCode() == null || location.getCode().trim().isEmpty()) {
            return "Location code is required";
        }
        
        if (location.getName() == null || location.getName().trim().isEmpty()) {
            return "Location name is required";
        }

        if (parentCode != null && !parentCode.trim().isEmpty()) {
            Optional<Location> getParent = locationRepository.findByCode(parentCode);

            if (getParent.isPresent()) {
                location.setParent(getParent.get());

                if (!locationRepository.existsByCode(location.getCode())) {
                    locationRepository.save(location);
                    return "Child saved successfully";
                } else {
                    return "Child with this code already exists";
                }

            } else {
                return "Parent with this code does not exist";
            }
        } else {
            if (!locationRepository.existsByCode(location.getCode())) {
                locationRepository.save(location);
                return "Parent saved successfully";
            } else {
                return "Parent with this code already exists";
            }
        }
    }

    public Optional<Location> getLocation(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        return locationRepository.findByCode(code);
    }

    public String getProvinceBySector(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "Code cannot be null or empty";
        }
        
        Optional<Location> response = locationRepository.findByCode(code);
        if (response.isPresent()) {
            Location location = response.get();
            
            if (location.getParent() == null) {
                return "Location has no parent";
            }
            
            if (location.getParent().getParent() == null) {
                return "Location has no province";
            }
            
            return location.getParent().getParent().getName();
        } else {
            return "No location found";
        }
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public List<Location> getLocationsByType(Elocation type) {
        return locationRepository.findByType(type);
    }

    public List<Location> getRootLocations() {
        return locationRepository.findByParentIsNull();
    }

    public List<Location> getChildrenByParentCode(String parentCode) {
        Optional<Location> parent = locationRepository.findByCode(parentCode);
        if (parent.isPresent()) {
            return locationRepository.findByParent(parent.get());
        }
        return List.of();
    }

    public String updateLocation(String code, Location updatedLocation) {
        if (code == null || code.trim().isEmpty()) {
            return "Code cannot be null or empty";
        }
        
        Optional<Location> existingLocation = locationRepository.findByCode(code);
        if (!existingLocation.isPresent()) {
            return "Location not found";
        }
        
        Location location = existingLocation.get();
        
        if (updatedLocation.getName() != null && !updatedLocation.getName().trim().isEmpty()) {
            location.setName(updatedLocation.getName());
        }
        
        if (updatedLocation.getCode() != null && !updatedLocation.getCode().trim().isEmpty()) {
            if (!location.getCode().equals(updatedLocation.getCode()) && 
                locationRepository.existsByCode(updatedLocation.getCode())) {
                return "New code already exists";
            }
            location.setCode(updatedLocation.getCode());
        }
        
        if (updatedLocation.getType() != null) {
            location.setType(updatedLocation.getType());
        }
        
        locationRepository.save(location);
        return "Location updated successfully";
    }

    public String deleteLocation(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "Code cannot be null or empty";
        }
        
        Optional<Location> location = locationRepository.findByCode(code);
        if (!location.isPresent()) {
            return "Location not found";
        }
        
        long childrenCount = locationRepository.countChildrenByParentId(location.get().getLocationId());
        if (childrenCount > 0) {
            return "Cannot delete location with children";
        }
        
        locationRepository.delete(location.get());
        return "Location deleted successfully";
    }

    public List<Location> searchLocationsByName(String name) {
        return locationRepository.searchByName(name);
    }

    public boolean existsByCode(String code) {
        return locationRepository.existsByCode(code);
    }
}