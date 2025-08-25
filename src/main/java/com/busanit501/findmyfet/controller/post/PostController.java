package com.busanit501.findmyfet.controller.post;

import com.busanit501.findmyfet.dto.post.*; // DTO 한번에 import
import com.busanit501.findmyfet.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal; // Spring Security 설정 후 import
// import com.busanit501.findmyfet.security.UserDetailsImpl; // UserDetails 구현체 import
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 2.1 게시글 목록 조회
    @GetMapping
    public ResponseEntity<List<PostListResponseDto>> getPostList() {
        List<PostListResponseDto> posts = postService.findAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 2.2 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponseDto> getPost(@PathVariable Long postId) {
        PostDetailResponseDto responseDto = postService.findPostById(postId);
        return ResponseEntity.ok(responseDto);
    }

    // 2.3 게시글 작성
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createPost(
            @RequestPart("requestDto") PostCreateRequestDto requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        // @AuthenticationPrincipal UserDetailsImpl userDetails) { // TODO: Security 연동 후 주석 해제

        // Long userId = userDetails.getUser().getUserid(); // TODO: Security 연동 후 이 코드로 교체
        Long tempUserId = 1L; // <<<<<<<<<<<<<<<<<<<< 임시 사용자 ID (테스트용)

        Long postId = postService.createPost(requestDto, images, tempUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 성공적으로 등록되었습니다. ID: " + postId);
    }

    // 2.4 게시글 수정
    @PutMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updatePost(
            @PathVariable Long postId,
            @RequestPart("requestDto") PostUpdateRequestDto requestDto,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        // @AuthenticationPrincipal UserDetailsImpl userDetails) { // TODO: Security 연동 후 주석 해제

        // Long userId = userDetails.getUser().getUserid(); // TODO: Security 연동 후 이 코드로 교체
        Long tempUserId = 1L; // <<<<<<<<<<<<<<<<<<<< 임시 사용자 ID (테스트용)

        postService.updatePost(postId, requestDto, newImages, tempUserId);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다. ID: " + postId);
    }

    // 2.5 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        // @AuthenticationPrincipal UserDetailsImpl userDetails) { // TODO: Security 연동 후 주석 해제

        // Long userId = userDetails.getUser().getUserid(); // TODO: Security 연동 후 이 코드로 교체
        Long tempUserId = 1L; // <<<<<<<<<<<<<<<<<<<< 임시 사용자 ID (테스트용)

        postService.deletePost(postId, tempUserId);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다. ID: " + postId);
    }

    // 2.6 찾기 완료 처리
    @PutMapping("/{postId}/complete")
    public ResponseEntity<String> completePost(@PathVariable Long postId) {
        // @AuthenticationPrincipal UserDetailsImpl userDetails) { // TODO: Security 연동 후 주석 해제

        // Long userId = userDetails.getUser().getUserid(); // TODO: Security 연동 후 이 코드로 교체
        Long tempUserId = 1L; // <<<<<<<<<<<<<<<<<<<< 임시 사용자 ID (테스트용)

        postService.completePost(postId, tempUserId);
        return ResponseEntity.ok("찾기 완료 처리되었습니다. ID: " + postId);
    }

    // 2.7 내가 작성한 게시글 목록
    @GetMapping("/my")
    public ResponseEntity<List<MyPostResponseDto>> getMyPosts() {
        // @AuthenticationPrincipal UserDetailsImpl userDetails) { // TODO: Security 연동 후 주석 해제

        // Long userId = userDetails.getUser().getUserid(); // TODO: Security 연동 후 이 코드로 교체
        Long tempUserId = 1L; // <<<<<<<<<<<<<<<<<<<< 임시 사용자 ID (테스트용)

        List<MyPostResponseDto> myPosts = postService.findMyPosts(tempUserId);
        return ResponseEntity.ok(myPosts);
    }
}