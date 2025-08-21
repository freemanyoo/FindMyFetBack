package com.busanit501.findmyfet.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_pet_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostPetPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String author;

    // 분실 날짜
    @Column(name = "lost_date")
    private LocalDate lostDate;

    // 지역 정보
    @Column(name = "city_province", length = 20)
    private String cityProvince; // 시·도

    @Column(name = "district", length = 30)
    private String district; // 군/구

    // 동물 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "animal_type")
    private AnimalType animalType;

    @Column(name = "breed", length = 50)
    private String breed;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 검색 완료 여부
    @Column(name = "is_found")
    private Boolean isFound = false;

    // 연락처
    @Column(name = "contact", length = 50)
    private String contact;

    // 이미지 URL (여러 개인 경우 JSON이나 별도 테이블로 관리 가능)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enum 정의
    public enum AnimalType {
        DOG("개"),
        CAT("고양이"),
        OTHER("기타");

        private final String korean;

        AnimalType(String korean) {
            this.korean = korean;
        }

        public String getKorean() {
            return korean;
        }
    }

    public enum Gender {
        MALE("수컷"),
        FEMALE("암컷"),
        UNKNOWN("모름");

        private final String korean;

        Gender(String korean) {
            this.korean = korean;
        }

        public String getKorean() {
            return korean;
        }
    }
}