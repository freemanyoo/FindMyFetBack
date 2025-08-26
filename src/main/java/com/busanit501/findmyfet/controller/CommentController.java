package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // ✅ MultipartFile import

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentList(@PathVariable Long postId) {
        log.info("getCommentList 호출, postId: " + postId);
        List<CommentDTO> commentList = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(commentList);
    }

    // ✅✅✅ [핵심 수정] ✅✅✅
    // consumes = MediaType.MULTIPART_FORM_DATA_VALUE -> 이제 이 API는 multipart/form-data 타입만 받습니다.
    @PostMapping(value = "/posts/{postId}/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long postId,
                                                    // ✅ @RequestBody -> @RequestPart로 변경
                                                    // value="commentDTO"는 프론트에서 보낼 때의 key값입니다.
                                                    @RequestPart("commentDTO") CommentDTO commentDTO,
                                                    // ✅ 이미지 파일은 @RequestPart("imageFile")로 받습니다.
                                                    // required = false는 이미지를 보내지 않아도 된다는 의미입니다.
                                                    @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
                                                    Principal principal) {
        String mid = principal.getName();
        log.info("createComment 호출, postId: {}, by user: {}", postId, mid);
        log.info("imageFile: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));

        commentDTO.setPostId(postId);
        // ✅ 서비스 메서드에 imageFile을 전달합니다.
        CommentDTO createdComment = commentService.createComment(commentDTO, mid, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(createdComment);
    }

    // ✅✅✅ [핵심 수정] ✅✅✅
    @PutMapping(value = "/comments/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId,
                                                    @RequestPart("commentDTO") CommentDTO commentDTO,
                                                    @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
                                                    Principal principal) {
        String mid = principal.getName();
        log.info("updateComment 호출, commentId: {}, by user: {}", commentId, mid);

        // ✅ 서비스 메서드에 imageFile을 전달합니다.
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO, mid, imageFile);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              Principal principal) {
        String mid = principal.getName();
        log.info("deleteComment 호출, commentId: {}, by user: {}", commentId, mid);
        commentService.deleteComment(commentId, mid);
        return ResponseEntity.noContent().build();
    }
}