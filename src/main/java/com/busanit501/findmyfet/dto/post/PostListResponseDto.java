package com.busanit501.findmyfet.dto.post;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.user.AuthorDto;
import lombok.Getter;

@Getter
public class PostListResponseDto {
    private Long postId;
    private String title;
    private String animalName;
    private String thumbnailUrl; // 디폴트값 null;
    private String postType;
    private String status;

    private AuthorDto author; // AuthorDto 필드 추가

    // 엔티티를 DTO로 변환하는 생성자
    public PostListResponseDto(Post entity) {
        this.postId = entity.getId();
        this.title = entity.getTitle();
        this.animalName = entity.getAnimalName();
        this.postType = entity.getPostType().name();
        this.status = entity.getStatus().name();
//        this.createdAt = entity.getCreatedAt();
        if (entity.getUser() != null) {
            this.author = new AuthorDto(entity.getUser());
        }
        // 게시글에 이미지가 존재하면, 첫 번째 이미지의 URL을 thumbnailUrl로 설정합니다.
        // 이미지가 없으면 기존처럼 null로 유지
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            this.thumbnailUrl = entity.getImages().get(0).getImageUrl();
        }
    }
}