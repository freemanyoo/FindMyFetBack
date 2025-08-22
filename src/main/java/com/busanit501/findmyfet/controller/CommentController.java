package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Log4j2
public class CommentController {

    private final CommentService commentService;

    // ... 기존 코드 유지

    // 댓글 작성
    @PostMapping("/{postId}")
    public ResponseEntity<?> createComment(@PathVariable Long postId, @RequestBody CommentDTO commentDTO) {
        log.info("createComment 호출, postId: " + postId);
        try {
            commentDTO.setPostId(postId);
            // TODO: 인증된 사용자 ID를 commentDTO.setUserId(userId); 로 설정해야 함
            CommentDTO createdComment = commentService.createComment(commentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (IllegalStateException e) {
            log.error("댓글 작성 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("댓글 작성 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ... 기존 코드 유지
}