package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.LostPetPost;
import com.busanit501.findmyfet.dto.LostPetSearchCriteria;
import com.busanit501.findmyfet.repository.LostPetPostRepository;
import com.busanit501.findmyfet.repository.LostPetPostSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LostPetPostService {

    private final LostPetPostRepository lostPetPostRepository;

    /**
     * 검색 조건에 따라 분실 신고 게시글을 검색합니다.
     */
    public Page<LostPetPost> searchLostPetPosts(LostPetSearchCriteria criteria) {
        // Specification 생성
        Specification<LostPetPost> spec = LostPetPostSpecification.withCriteria(criteria);

        // 정렬 설정
        Sort sort = createSort(criteria.getSortBy(), criteria.getSortDir());

        // 페이지 설정
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        // 검색 실행
        return lostPetPostRepository.findAll(spec, pageable);
    }

    /**
     * 모든 동물 타입 목록을 조회합니다.
     */
    public List<Map<String, String>> getAllAnimalTypes() {
        List<Map<String, String>> animalTypes = new ArrayList<>();

        for (LostPetPost.AnimalType type : LostPetPost.AnimalType.values()) {
            Map<String, String> animalType = new HashMap<>();
            animalType.put("value", type.name());
            animalType.put("label", type.getKorean());
            animalTypes.add(animalType);
        }

        return animalTypes;
    }

    /**
     * 모든 성별 목록을 조회합니다.
     */
    public List<Map<String, String>> getAllGenders() {
        List<Map<String, String>> genders = new ArrayList<>();

        for (LostPetPost.Gender gender : LostPetPost.Gender.values()) {
            Map<String, String> genderMap = new HashMap<>();
            genderMap.put("value", gender.name());
            genderMap.put("label", gender.getKorean());
            genders.add(genderMap);
        }

        return genders;
    }

    /**
     * 모든 시·도 목록을 조회합니다.
     */
    public List<String> getAllCityProvinces() {
        return lostPetPostRepository.findDistinctCityProvinces();
    }

    /**
     * 특정 시·도의 군/구 목록을 조회합니다.
     */
    public List<String> getDistrictsByCity(String cityProvince) {
        if (cityProvince == null || cityProvince.trim().isEmpty()) {
            throw new IllegalArgumentException("시·도 정보가 필요합니다.");
        }

        return lostPetPostRepository.findDistinctDistrictsByCityProvince(cityProvince);
    }

    /**
     * 특정 동물 타입의 품종 목록을 조회합니다.
     */
    public List<String> getBreedsByAnimalType(LostPetPost.AnimalType animalType) {
        if (animalType == null) {
            throw new IllegalArgumentException("동물 타입 정보가 필요합니다.");
        }

        return lostPetPostRepository.findDistinctBreedsByAnimalType(animalType);
    }

    /**
     * 게시글 ID로 특정 게시글을 조회합니다.
     */
    public LostPetPost findById(Long id) {
        return lostPetPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 게시글의 검색 완료 상태를 토글합니다.
     */
    @Transactional
    public LostPetPost toggleFoundStatus(Long id) {
        LostPetPost post = findById(id);

        // 현재 상태의 반대로 설정
        post.setIsFound(!post.getIsFound());

        return lostPetPostRepository.save(post);
    }

    /**
     * 정렬 조건을 생성합니다.
     */
    private Sort createSort(String sortBy, String sortDir) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortBy);
    }
}