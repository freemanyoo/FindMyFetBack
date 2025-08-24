package com.busanit501.findmyfet.controller.post;

import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.busanit501.findmyfet.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostListResponseDto>> getPostList() {
        List<PostListResponseDto> posts = postService.findAllPosts();
        return ResponseEntity.ok(posts);
    }
}