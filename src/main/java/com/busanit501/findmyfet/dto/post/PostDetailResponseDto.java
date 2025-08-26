package com.busanit501.findmyfet.dto.post;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.user.AuthorDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 상세 조회 DTO
@Getter
public class PostDetailResponseDto {

    private Long postId;
    private String title;
    private String content;
    private String animalName;
    private int animalAge;
    private String animalCategory;
    private String animalBreed;
    private LocalDateTime lostTime;

    private double latitude;
    private double longitude;

    private String location; // 잃어버린장소 추가 250825

    private String postType;
    private String status;
    private LocalDateTime createdAt;
    private AuthorDto author; // TODO: User 기능 연동 후 추가
    private List<String> imageUrls; // 이미지 URL 목록

    public PostDetailResponseDto(Post entity) {
        this.postId = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.animalName = entity.getAnimalName();
        this.animalAge = entity.getAnimalAge();
        this.animalCategory = entity.getAnimalCategory();
        this.animalBreed = entity.getAnimalBreed();
        this.lostTime = entity.getLostTime();

        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();

        this.location = entity.getLocation(); // 잃어버린장소 추가 250825

        this.postType = entity.getPostType().name();
        this.status = entity.getStatus().name();
        this.createdAt = entity.getCreatedAt();
        this.imageUrls = entity.getImages().stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
        if (entity.getUser() != null) { // getMember() -> getUser()
            this.author = new AuthorDto(entity.getUser()); // getMember() -> getUser()
        }
    }
}
