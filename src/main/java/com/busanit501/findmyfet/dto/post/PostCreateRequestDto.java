package com.busanit501.findmyfet.dto.post;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.PostType;
import com.busanit501.findmyfet.domain.post.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
// 게시글 작성 DTO
@Getter
@Setter
public class PostCreateRequestDto {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;

    private String animalName;
    private int animalAge;
    private String animalCategory;
    private String animalBreed;
    private LocalDateTime lostTime;

    private double latitude;
    private double longitude;

    private String location; // 잃어버린장소 추가 250825
    private PostType postType;

    // DTO를 Entity로 변환하는 메서드
    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .animalName(animalName)
                .animalAge(animalAge)
                .animalCategory(animalCategory)
                .animalBreed(animalBreed)
                .lostTime(lostTime)

                .latitude(latitude)
                .longitude(longitude)

                .location(location) // <<<<<<<<<<<< 추가 250825

                .postType(postType)
                .status(Status.ACTIVE) // 게시글 작성 시 기본 상태는 ACTIVE
                .build();
    }
}
