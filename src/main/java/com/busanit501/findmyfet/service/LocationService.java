// src/main/java/com/busanit501/findmyfet/service/LocationService.java
package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.dto.LocationDTO;
import com.busanit501.findmyfet.domain.Location;
import com.busanit501.findmyfet.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper; // ModelMapper 주입

    public Location saveLocation(LocationDTO locationDto) {
        // ModelMapper를 사용하여 DTO를 엔티티로 변환
        Location location = modelMapper.map(locationDto, Location.class);
        return locationRepository.save(location);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
}