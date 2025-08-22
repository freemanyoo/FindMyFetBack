package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.domain.LostPetPost;
import com.busanit501.findmyfet.dto.LostPetSearchCriteria;
import com.busanit501.findmyfet.service.LostPetPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lost-pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // React 앱에서 호출을 위한 CORS 설정
@Slf4j
public class LostPetPostController {

    private final LostPetPostService lostPetPostService;

    /**
     * 분실 신고 게시글 검색 및 필터링
     * GET /api/lost-pets/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchLostPets(
            @Valid @ModelAttribute LostPetSearchCriteria criteria) {

        try {
            Page<LostPetPost> result = lostPetPostService.searchLostPetPosts(criteria);

            Map<String, Object> response = new HashMap<>();
            response.put("content", result.getContent());
            response.put("page", result.getNumber());
            response.put("size", result.getSize());
            response.put("totalElements", result.getTotalElements());
            response.put("totalPages", result.getTotalPages());
            response.put("first", result.isFirst());
            response.put("last", result.isLast());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("분실 신고 게시글 검색 중 오류 발생", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "검색 중 오류가 발생했습니다.");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 필터 옵션을 위한 기본 데이터 조회
     * GET /api/lost-pets/filter-options
     */
    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        try {
            Map<String, Object> options = new HashMap<>();

            // 동물 타입 목록
            options.put("animalTypes", lostPetPostService.getAllAnimalTypes());

            // 성별 목록
            options.put("genders", lostPetPostService.getAllGenders());

            // 시·도 목록
            options.put("cityProvinces", lostPetPostService.getAllCityProvinces());

            return ResponseEntity.ok(options);

        } catch (Exception e) {
            log.error("필터 옵션 조회 중 오류 발생", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "필터 옵션 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 특정 시·도의 군/구 목록 조회
     * GET /api/lost-pets/districts?cityProvince=서울특별시
     */
    @GetMapping("/districts")
    public ResponseEntity<List<String>> getDistricts(@RequestParam String cityProvince) {
        try {
            List<String> districts = lostPetPostService.getDistrictsByCity(cityProvince);
            return ResponseEntity.ok(districts);

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 파라미터: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("군/구 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 특정 동물 타입의 품종 목록 조회
     * GET /api/lost-pets/breeds?animalType=DOG
     */
    @GetMapping("/breeds")
    public ResponseEntity<List<String>> getBreeds(@RequestParam LostPetPost.AnimalType animalType) {
        try {
            List<String> breeds = lostPetPostService.getBreedsByAnimalType(animalType);
            return ResponseEntity.ok(breeds);

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 파라미터: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("품종 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 특정 게시글 상세 조회
     * GET /api/lost-pets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<LostPetPost> getLostPetPost(@PathVariable Long id) {
        try {
            LostPetPost post = lostPetPostService.findById(id);
            return ResponseEntity.ok(post);

        } catch (RuntimeException e) {
            log.warn("게시글 조회 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 검색 완료 상태 토글
     * PATCH /api/lost-pets/{id}/toggle-found
     */
    @PatchMapping("/{id}/toggle-found")
    public ResponseEntity<Map<String, Object>> toggleFoundStatus(@PathVariable Long id) {
        try {
            LostPetPost updatedPost = lostPetPostService.toggleFoundStatus(id);

            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedPost.getId());
            response.put("isFound", updatedPost.getIsFound());
            response.put("message", updatedPost.getIsFound() ? "검색이 완료되었습니다." : "검색 중으로 변경되었습니다.");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("상태 토글 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("상태 토글 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}