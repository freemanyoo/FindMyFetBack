package com.busanit501.findmyfet.dto.post;

import com.busanit501.findmyfet.domain.post.Post;
import lombok.Getter;

@Getter
public class PostListResponseDto {
    private Long postId;
    private String title;
    private String animalName;
    private String thumbnailUrl; // 일단은 null 또는 빈 값으로
    private String postType;
    private String status;
//    private LocalDateTime createdAt;
    // private AuthorDto author; // User 기능 연동 후 추가

    // 엔티티를 DTO로 변환하는 생성자
    public PostListResponseDto(Post entity) {
        this.postId = entity.getId();
        this.title = entity.getTitle();
        this.animalName = entity.getAnimalName();
        this.postType = entity.getPostType().name();
        this.status = entity.getStatus().name();
//        this.createdAt = entity.getCreatedAt();
    }
}