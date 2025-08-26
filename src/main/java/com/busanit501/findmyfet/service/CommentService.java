package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.Comment;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.repository.CommentRepository;
import com.busanit501.findmyfet.repository.post.PostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // ✅ MultipartFile import 추가

import java.io.File; // ✅ File import 추가
import java.io.IOException; // ✅ IOException import 추가
import java.nio.file.Files; // ✅ Files import 추가
import java.nio.file.Path; // ✅ Path import 추가
import java.nio.file.Paths; // ✅ Paths import 추가
import java.util.List;
import java.util.UUID; // ✅ UUID import 추가
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2 // ✅ Log4j2 추가
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // ✅ application.properties에 설정한 파일 업로드 경로를 주입받습니다.
    @Value("${upload.path}")
    private String uploadPath;

    // ✅ 수정: MultipartFile 매개변수 추가
    public CommentDTO createComment(CommentDTO commentDTO, String loginId, MultipartFile imageFile) {
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (post.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("이미 찾기 완료된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 이미지 파일 처리 로직 추가
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = saveImage(imageFile); // 아래에 새로 만든 메서드 호출
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(commentDTO.getContent())
                .imageUrl(imageUrl) // ✅ 저장된 이미지 URL 설정
                .build();

        Comment savedComment = commentRepository.save(comment);
        return entityToDto(savedComment);
    }

    // ✅ 수정: MultipartFile 매개변수 추가
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO, String loginId, MultipartFile imageFile) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new IllegalStateException("댓글 수정 권한이 없습니다.");
        }

        // ✅ 새로운 이미지 파일이 있으면 기존 이미지 삭제 후 저장
        String imageUrl = comment.getImageUrl(); // 기본은 기존 이미지 URL
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageUrl != null) {
                deleteImage(imageUrl); // 기존 이미지 파일 삭제
            }
            imageUrl = saveImage(imageFile); // 새 이미지 파일 저장
        }

        // ✅ content, imageUrl 업데이트
        comment.updateContent(commentDTO.getContent(), imageUrl);

        Comment updatedComment = commentRepository.save(comment);
        return entityToDto(updatedComment);
    }

    public void deleteComment(Long commentId, String loginId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new IllegalStateException("댓글 삭제 권한이 없습니다.");
        }

        // ✅ 댓글에 이미지가 있으면 파일 시스템에서 삭제
        if (comment.getImageUrl() != null) {
            deleteImage(comment.getImageUrl());
        }

        commentRepository.deleteById(commentId);
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostPostId(postId);
        return comments.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // ✅ 이미지 파일을 서버에 저장하고, 접근 가능한 URL(경로)을 반환하는 메서드
    private String saveImage(MultipartFile imageFile) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            // 파일 이름이 중복되지 않도록 UUID를 사용
            String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path savedPath = Paths.get(uploadPath, savedFilename);

            // uploadPath 디렉토리가 없으면 생성
            Files.createDirectories(savedPath.getParent());

            imageFile.transferTo(savedPath.toFile());

            // DB에 저장할 상대 경로 반환 (예: /upload/uuid_image.jpg)
            // 실제 서비스에서는 /images/uuid_image.jpg 와 같이 URL 매핑을 통해 접근
            return savedFilename;
        } catch (IOException e) {
            log.error("이미지 파일 저장 실패", e);
            throw new RuntimeException("이미지 파일 저장에 실패했습니다.");
        }
    }

    // ✅ 이미지 파일을 삭제하는 메서드
    private void deleteImage(String filename) {
        try {
            Path filePath = Paths.get(uploadPath, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("이미지 파일 삭제 실패: " + filename, e);
            // 파일 삭제 실패는 서비스의 흐름을 막을 필요는 없으므로 예외를 던지지 않음
        }
    }


    private CommentDTO entityToDto(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getUserId())
                .content(comment.getContent())
                .imageUrl(comment.getImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}