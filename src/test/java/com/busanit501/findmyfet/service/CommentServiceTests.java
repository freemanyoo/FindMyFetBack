package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.Comment;

import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.repository.CommentRepository;
import com.busanit501.findmyfet.repository.post.PostRepository;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User mockUser;
    private com.busanit501.findmyfet.domain.post.Post mockPost;
    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().loginId("testUser").userId(1L).build();
        mockPost = com.busanit501.findmyfet.domain.post.Post.builder()
                .id(1L)
                .status(Status.ACTIVE)
                .build();

        commentDTO = CommentDTO.builder()
                .postId(1L)
                .content("테스트 댓글")
                .build();
    }

    @Test
    @DisplayName("댓글 작성 성공 테스트")
    void createComment_Success() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));
        given(userRepository.findByLoginId(anyString())).willReturn(Optional.of(mockUser));
        given(commentRepository.save(any(Comment.class))).willAnswer(invocation -> invocation.getArgument(0));

        CommentDTO result = commentService.createComment(commentDTO, "testUser", null);

        assertNotNull(result);
        assertEquals("테스트 댓글", result.getContent());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("실패 테스트: 완료된 게시글에 댓글 작성 시 예외 발생")
    void createComment_Fail_WhenPostIsCompleted() {
        mockPost = com.busanit501.findmyfet.domain.post.Post.builder().id(mockPost.getId()).status(Status.COMPLETED).build();

        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            commentService.createComment(commentDTO, "testUser", null);
        });

        assertEquals("이미 찾기 완료된 게시글에는 댓글을 작성할 수 없습니다.", exception.getMessage());

        verify(userRepository, never()).findByLoginId(anyString());
        verify(commentRepository, never()).save(any(Comment.class));
    }
}