package com.busanit501.findmyfet.repository;

import com.busanit501.findmyfet.domain.Location;   // ✅ 도메인 Location 엔티티를 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // 특정 위도 및 경도 범위 내의 위치를 찾는 메서드
    List<Location> findByLatitudeBetweenAndLongitudeBetween(
            Double minLat, Double maxLat,
            Double minLng, Double maxLng
    );
}
