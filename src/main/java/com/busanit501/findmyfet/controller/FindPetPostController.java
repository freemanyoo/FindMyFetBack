package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.post.FindPetSearchCriteria;
import com.busanit501.findmyfet.service.FindPetPostService;
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
@RequestMapping("/api/find-pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // React 앱에서 호출을 위한 CORS 설정
@Slf4j
public class FindPetPostController {

    private final FindPetPostService findPetPostService;

    /**
     * 분실 신고 게시글 검색 및 필터링
     * GET /api/find-pets/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFindPets(
            @Valid @ModelAttribute FindPetSearchCriteria criteria) {

        try {
            Page<Post> result = findPetPostService.searchFindPetPosts(criteria);

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
     * GET /api/find-pets/filter-options
     */
    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        try {
            Map<String, Object> options = new HashMap<>();

            // 동물 카테고리 목록 (animalCategory 기반)
            options.put("animalCategories", findPetPostService.getAllAnimalCategories());

            // 게시글 타입 목록
            options.put("postTypes", findPetPostService.getAllPostTypes());

            // 지역 목록 (location 기반)
            options.put("locations", findPetPostService.getAllLocations());

            return ResponseEntity.ok(options);

        } catch (Exception e) {
            log.error("필터 옵션 조회 중 오류 발생", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "필터 옵션 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 특정 동물 카테고리의 품종 목록 조회
     * GET /api/find-pets/breeds?animalCategory=개
     */
    @GetMapping("/breeds")
    public ResponseEntity<List<String>> getBreeds(@RequestParam String animalCategory) {
        try {
            List<String> breeds = findPetPostService.getBreedsByAnimalCategory(animalCategory);
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
     * GET /api/find-pets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getFindPetPost(@PathVariable Long id) {
        try {
            Post post = findPetPostService.findById(id);
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
     * 게시글 상태 완료로 변경
     * PATCH /api/find-pets/{id}/complete
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completePost(@PathVariable Long id) {
        try {
            Post updatedPost = findPetPostService.completePost(id);

            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedPost.getId());
            response.put("status", updatedPost.getStatus());
            response.put("message", "게시글이 완료 처리되었습니다.");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("상태 변경 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}