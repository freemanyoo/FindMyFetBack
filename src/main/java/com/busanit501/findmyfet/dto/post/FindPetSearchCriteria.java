package com.busanit501.findmyfet.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindPetSearchCriteria {

    // 검색 조건
    private String title;        // 제목 검색 (부분 일치)
    private String author;       // 작성자 검색 (부분 일치)

    // 필터 조건
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lostDateFrom;  // 분실 날짜 시작

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lostDateTo;    // 분실 날짜 종료

    private String cityProvince;     // 시·도
    private String district;         // 군/구

    private FindPetPost.AnimalType animalType; // 동물 종류
    private String breed;            // 품종
    private FindPetPost.Gender gender; // 성별

    // 토글 옵션
    private Boolean isFound;         // 검색 완료 여부 (null이면 전체, true면 완료, false면 미완료)

    // 페이징 및 검증 추가
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    @Builder.Default
    private int page = 0;           // 페이지 번호 (0부터 시작)

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    @Builder.Default
    private int size = 20;          // 페이지 크기

    @Builder.Default
    private String sortBy = "createdAt"; // 정렬 필드

    @Builder.Default
    private String sortDir = "DESC"; // 정렬 방향

    // 헬퍼 메서드들
    public boolean hasTitle() {
        return title != null && !title.trim().isEmpty();
    }

    public boolean hasAuthor() {
        return author != null && !author.trim().isEmpty();
    }

    public boolean hasDateRange() {
        return lostDateFrom != null || lostDateTo != null;
    }

    public boolean hasCityProvince() {
        return cityProvince != null && !cityProvince.trim().isEmpty();
    }

    public boolean hasDistrict() {
        return district != null && !district.trim().isEmpty();
    }

    public boolean hasBreed() {
        return breed != null && !breed.trim().isEmpty();
    }

    // 날짜 범위 유효성 검증
    public boolean isDateRangeValid() {
        if (lostDateFrom != null && lostDateTo != null) {
            return !lostDateFrom.isAfter(lostDateTo);
        }
        return true;
    }
}