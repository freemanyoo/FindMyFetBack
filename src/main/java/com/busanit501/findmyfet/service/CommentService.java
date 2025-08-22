package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.Comment;
import com.busanit501.findmyfet.domain.Post;
import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.repository.CommentRepository;
import com.busanit501.findmyfet.repository.PostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository; // 게시글 존재 여부 확인용
    private final UserRepository userRepository; // 사용자 존재 여부 확인용

    // 게시글에 댓글 작성
    public CommentDTO createComment(CommentDTO commentDTO) {
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // ✅ DAY 3 목표: '찾기 완료'된 게시글에 댓글 작성 방지 로직 추가
        if (post.isCompleted()) {
            throw new IllegalStateException("이미 찾기 완료된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(commentDTO.getContent())
                .imageUrl(commentDTO.getImageUrl())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return entityToDto(savedComment);
    }

    // 댓글 수정
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // TODO: 작성자 본인인지 확인하는 로직 추가

        Comment updatedComment = Comment.builder()
                .commentId(comment.getCommentId())
                .post(comment.getPost())
                .user(comment.getUser())
                .content(commentDTO.getContent())
                .imageUrl(commentDTO.getImageUrl())
                .createdAt(comment.getCreatedAt())
                .build();

        commentRepository.save(updatedComment);
        return entityToDto(updatedComment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        // TODO: 작성자 본인인지 확인하는 로직 추가
        commentRepository.deleteById(commentId);
    }

    // 특정 게시글의 모든 댓글 조회 (DAY 1 구현부)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostPostId(postId);
        return comments.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // 엔티티 -> DTO 변환 메서드
    private CommentDTO entityToDto(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getUserId())
                .content(comment.getContent())
                .imageUrl(comment.getImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}