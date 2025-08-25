package com.busanit501.findmyfet.domain.post;

import com.busanit501.findmyfet.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor // Builder 패턴 쓰기위해서 모든 필드에 생성자 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"images"}) // images 필드는 ToString에서 제외
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    // ERD를 참고하여 나머지 필드를 모두 추가합니다.
    @Column(nullable = false)
    private String title;

    @Lob // 대용량 텍스트를 위한 @Lob 어노테이션
    private String content;

    private String animalName;
    private int animalAge;
    private String animalCategory; // 예시: "개", "고양이" 등
    private String animalBreed; // 품종

    private LocalDateTime lostTime; // 실종 시간

//    private double latitude;  // 위도
//    private double longitude; // 경도

    @Enumerated(EnumType.STRING) // "MISSING", "SHELTER" 같은 문자열로 저장
    @Column(nullable = false)
    private PostType postType;

    @Enumerated(EnumType.STRING) // "ACTIVE", "COMPLETED"
    @Column(nullable = false)
    private Status status;

//    private LocalDateTime createdAt;

    // 1:N, Post(1) : Image(N)
    // Post가 삭제되면 연관된 Image도 함께 삭제되도록 cascade 설정
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 빌더 패턴 사용 시 기본값으로 초기화
    private List<Image> images = new ArrayList<>();

    // TODO: User 엔티티와 연관관계 설정 (DAY 1 후반 또는 DAY 2)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id")
    // private User user;

    // 연관관계 편의 메서드
    public void addImage(Image image) {
        images.add(image);
        image.setPost(this);
    }

    // 비즈니스 로직 : 업데이트
    public void update(String title, String content, String animalName, /*...모든 필드...*/ double latitude, double longitude) {
        this.title = title;
        this.content = content;
        this.animalName = animalName;
        // ... this.필드 = 파라미터;
//        this.latitude = latitude;
//        this.longitude = longitude;
    }

    //Post 엔티티 스스로 자신의 상태를 바꾸도록 메서드
    // 비즈니스 로직 : 찾기 완료 처리
    public void complete() {
        this.status = Status.COMPLETED;
    }
}
