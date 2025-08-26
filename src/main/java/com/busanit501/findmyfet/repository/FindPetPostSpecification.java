package com.busanit501.findmyfet.repository;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.post.FindPetSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> withCriteria(FindPetSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 제목 검색 (부분 일치)
            if (criteria.hasTitle()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + criteria.getTitle().toLowerCase() + "%"
                ));
            }

            // 동물 이름 검색 (부분 일치)
            if (criteria.hasAnimalName()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("animalName")),
                        "%" + criteria.getAnimalName().toLowerCase() + "%"
                ));
            }

            // 분실 시간 범위
            if (criteria.getLostTimeFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("lostTime"),
                        criteria.getLostTimeFrom()
                ));
            }

            if (criteria.getLostTimeTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("lostTime"),
                        criteria.getLostTimeTo()
                ));
            }

            // 지역 필터 (부분 일치)
            if (criteria.hasLocation()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")),
                        "%" + criteria.getLocation().toLowerCase() + "%"
                ));
            }

            // 동물 카테고리 필터
            if (criteria.hasAnimalCategory()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("animalCategory"),
                        criteria.getAnimalCategory()
                ));
            }

            // 품종 필터
            if (criteria.hasAnimalBreed()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("animalBreed"),
                        criteria.getAnimalBreed()
                ));
            }

            // 게시글 타입 필터
            if (criteria.getPostType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("postType"),
                        criteria.getPostType()
                ));
            }

            // 상태 필터
            if (criteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        criteria.getStatus()
                ));
            }

            // 동물 나이 범위
            if (criteria.getAnimalAgeFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("animalAge"),
                        criteria.getAnimalAgeFrom()
                ));
            }

            if (criteria.getAnimalAgeTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("animalAge"),
                        criteria.getAnimalAgeTo()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}