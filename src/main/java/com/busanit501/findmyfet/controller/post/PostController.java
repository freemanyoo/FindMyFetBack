package com.busanit501.findmyfet.controller.post;

import com.busanit501.findmyfet.dto.post.PostCreateRequestDto;
import com.busanit501.findmyfet.dto.post.PostDetailResponseDto;
import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.busanit501.findmyfet.dto.post.PostUpdateRequestDto;
import com.busanit501.findmyfet.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts") // API의 기본 경로
public class PostController {

    private final PostService postService;

    // 게시글 전체목록 조회기능
    @GetMapping
    public ResponseEntity<List<PostListResponseDto>> getPostList() {
        List<PostListResponseDto> posts = postService.findAllPosts();
        return ResponseEntity.ok(posts);
    }

//    @RequestPart : JSON 데이터와 파일(이미지) 데이터를 함께 받기 위함
    // 등록(작성)기능
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createPost(
            @RequestPart("requestDto") PostCreateRequestDto requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Long postId = postService.createPost(requestDto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    // 상세조회기능
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponseDto> getPost(@PathVariable Long postId) {
        PostDetailResponseDto responseDto = postService.findPostById(postId);
        return ResponseEntity.ok(responseDto);
    }

    // 게시글 수정기능
    @PutMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updatePost(
            @PathVariable Long postId,
            @RequestPart("requestDto") PostUpdateRequestDto requestDto,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        postService.updatePost(postId, requestDto, newImages);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다. ID: " + postId);
    }

    // 게시글 삭제기능
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다. ID: " + postId);
    }

}