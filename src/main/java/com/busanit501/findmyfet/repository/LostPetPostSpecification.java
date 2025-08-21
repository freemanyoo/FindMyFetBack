package com.busanit501.findmyfet.repository;

import com.example.petfinder.dto.LostPetSearchCriteria;
import com.example.petfinder.entity.LostPetPost;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class LostPetPostSpecification {

    public static Specification<LostPetPost> withCriteria(LostPetSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 제목 검색 (부분 일치)
            if (criteria.hasTitle()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + criteria.getTitle().toLowerCase() + "%"
                ));
            }

            // 작성자 검색 (부분 일치)
            if (criteria.hasAuthor()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")),
                        "%" + criteria.getAuthor().toLowerCase() + "%"
                ));
            }

            // 분실 날짜 범위
            if (criteria.getLostDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("lostDate"),
                        criteria.getLostDateFrom()
                ));
            }

            if (criteria.getLostDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("lostDate"),
                        criteria.getLostDateTo()
                ));
            }

            // 시·도 필터
            if (criteria.hasCityProvince()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("cityProvince"),
                        criteria.getCityProvince()
                ));
            }

            // 군/구 필터
            if (criteria.hasDistrict()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("district"),
                        criteria.getDistrict()
                ));
            }

            // 동물 종류 필터
            if (criteria.getAnimalType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("animalType"),
                        criteria.getAnimalType()
                ));
            }

            // 품종 필터
            if (criteria.hasBreed()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("breed"),
                        criteria.getBreed()
                ));
            }

            // 성별 필터
            if (criteria.getGender() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("gender"),
                        criteria.getGender()
                ));
            }

            // 검색 완료 토글
            if (criteria.getIsFound() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("isFound"),
                        criteria.getIsFound()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}