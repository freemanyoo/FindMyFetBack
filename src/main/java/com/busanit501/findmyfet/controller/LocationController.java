// src/main/java/com/busanit501/findmyfet/controller/LocationController.java
package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.domain.Location;
import com.busanit501.findmyfet.dto.LocationDTO;
import com.busanit501.findmyfet.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationDTO> saveLocation(@RequestBody LocationDTO locationDto) {
        // LocationDto를 Service로 전달하고, Service에서 반환된 엔티티를 다시 DTO로 변환
        Location newLocation = locationService.saveLocation(locationDto);
        LocationDTO newLocationDTO = new LocationDTO();
        newLocationDTO.setLocationName(newLocation.getLocationName());
        newLocationDTO.setLatitude(newLocation.getLatitude());
        newLocationDTO.setLongitude(newLocation.getLongitude());

        return ResponseEntity.ok(newLocationDTO);
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        // 엔티티 리스트를 DTO 리스트로 변환하여 반환
        List<LocationDTO> locationDTOS = locations.stream()
                .map(location -> {
                    LocationDTO dto = new LocationDTO();
                    dto.setLocationName(location.getLocationName());
                    dto.setLatitude(location.getLatitude());
                    dto.setLongitude(location.getLongitude());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationDTOS);
    }
}