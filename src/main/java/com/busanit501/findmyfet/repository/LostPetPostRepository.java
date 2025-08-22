package com.busanit501.findmyfet.repository;

import com.busanit501.findmyfet.domain.LostPetPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostPetPostRepository extends JpaRepository<LostPetPost, Long>,
        JpaSpecificationExecutor<LostPetPost> {

    // 특정 시·도의 모든 군/구 조회 (필터 옵션용)
    @Query("SELECT DISTINCT p.district FROM LostPetPost p WHERE p.cityProvince = :cityProvince AND p.district IS NOT NULL ORDER BY p.district")
    List<String> findDistinctDistrictsByCityProvince(@Param("cityProvince") String cityProvince);

    // 동물 타입별 품종 조회 (필터 옵션용)
    @Query("SELECT DISTINCT p.breed FROM LostPetPost p WHERE p.animalType = :animalType AND p.breed IS NOT NULL ORDER BY p.breed")
    List<String> findDistinctBreedsByAnimalType(@Param("animalType") LostPetPost.AnimalType animalType);

    // 모든 시·도 조회 (필터 옵션용)
    @Query("SELECT DISTINCT p.cityProvince FROM LostPetPost p WHERE p.cityProvince IS NOT NULL ORDER BY p.cityProvince")
    List<String> findDistinctCityProvinces();
}