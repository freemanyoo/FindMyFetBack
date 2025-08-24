package com.busanit501.findmyfet.domain.post;

import com.busanit501.findmyfet.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    // ERD를 참고하여 나머지 필드를 모두 추가합니다.
    private String title;
    private String content;

    private String animalName;
    private int animalAge;
    // ... (animalCategory, animalBreed 등 ERD의 모든 컬럼)

    private double latitude;  // 위도
    private double longitude; // 경도

    @Enumerated(EnumType.STRING) // "MISSING", "SHELTER" 같은 문자열로 저장
    private PostType postType;

    @Enumerated(EnumType.STRING) // "ACTIVE", "COMPLETED"
    private Status status;

//    private LocalDateTime createdAt;

    // TODO: User 엔티티와 연관관계 설정 (DAY 1 후반 또는 DAY 2)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id")
    // private User user;
}
