package com.busanit501.findmyfet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FindPetPostRepository extends JpaRepository<FindPetPost, Long>, JpaSpecificationExecutor<FindPetPost> {

    /**
     * 모든 시·도 목록을 중복 제거하여 조회
     */
    @Query("SELECT DISTINCT f.cityProvince FROM FindPetPost f WHERE f.cityProvince IS NOT NULL ORDER BY f.cityProvince")
    List<String> findDistinctCityProvinces();

    /**
     * 특정 시·도의 모든 군/구 목록을 중복 제거하여 조회
     */
    @Query("SELECT DISTINCT f.district FROM FindPetPost f WHERE f.cityProvince = :cityProvince AND f.district IS NOT NULL ORDER BY f.district")
    List<String> findDistinctDistrictsByCityProvince(@Param("cityProvince") String cityProvince);

    /**
     * 특정 동물 타입의 모든 품종 목록을 중복 제거하여 조회
     */
    @Query("SELECT DISTINCT f.breed FROM FindPetPost f WHERE f.animalType = :animalType AND f.breed IS NOT NULL ORDER BY f.breed")
    List<String> findDistinctBreedsByAnimalType(@Param("animalType") FindPetPost.AnimalType animalType);

    /**
     * 특정 지역(시·도, 군/구)의 게시글 수 조회
     */
    @Query("SELECT COUNT(f) FROM FindPetPost f WHERE f.cityProvince = :cityProvince AND f.district = :district")
    long countByCityProvinceAndDistrict(@Param("cityProvince") String cityProvince, @Param("district") String district);

    /**
     * 검색 완료되지 않은 게시글만 조회
     */
    List<FindPetPost> findByIsFoundFalseOrderByCreatedAtDesc();

    
}