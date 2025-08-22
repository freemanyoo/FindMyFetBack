package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.domain.Post;
import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private CommentDTO mockCommentDto;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockCommentDto = CommentDTO.builder()
                .commentId(1L)
                .postId(1L)
                .userId(1L) // 로그인한 유저 ID
                .content("테스트 댓글 내용")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --- DAY 1 테스트: 댓글 목록 조회 (인증 불필요) ---
    @Test
    @DisplayName("게시글 ID로 댓글 목록을 조회하면 200 OK와 함께 댓글 리스트를 반환한다")
    void getCommentListTest() throws Exception {
        List<CommentDTO> mockComments = Arrays.asList(mockCommentDto, new CommentDTO());
        given(commentService.getCommentsByPostId(any(Long.class))).willReturn(mockComments);

        mockMvc.perform(get("/api/comment/list/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].commentId").value(1));
    }

    // --- DAY 2 & 3 테스트: 댓글 작성 (인증 필요) ---
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("댓글 작성 요청 시 201 Created와 함께 생성된 댓글을 반환한다")
    void createCommentTest_Success() throws Exception {
        given(commentService.createComment(any(CommentDTO.class))).willReturn(mockCommentDto);

        String jsonContent = objectMapper.writeValueAsString(mockCommentDto);

        mockMvc.perform(post("/api/comment/1")
                        .with(csrf()) // CSRF 토큰을 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.content").value("테스트 댓글 내용"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("완료된 게시글에 댓글 작성 시 400 Bad Request를 반환한다")
    void createCommentTest_PostCompleted_Failure() throws Exception {
        given(commentService.createComment(any(CommentDTO.class)))
                .willThrow(new IllegalStateException("이미 찾기 완료된 게시글에는 댓글을 작성할 수 없습니다."));

        String jsonContent = objectMapper.writeValueAsString(mockCommentDto);

        mockMvc.perform(post("/api/comment/1")
                        .with(csrf()) // CSRF 토큰을 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 찾기 완료된 게시글에는 댓글을 작성할 수 없습니다."));
    }

    // --- DAY 2 테스트: 댓글 수정 (인증 필요) ---
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("댓글 수정 요청 시 200 OK와 함께 수정된 댓글을 반환한다")
    void updateCommentTest() throws Exception {
        CommentDTO updatedDto = CommentDTO.builder()
                .commentId(1L)
                .postId(1L)
                .content("수정된 댓글 내용")
                .build();

        given(commentService.updateComment(any(Long.class), any(CommentDTO.class))).willReturn(updatedDto);

        String jsonContent = objectMapper.writeValueAsString(updatedDto);

        mockMvc.perform(put("/api/comment/1")
                        .with(csrf()) // CSRF 토큰을 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 댓글 내용"));
    }

    // --- DAY 2 테스트: 댓글 삭제 (인증 필요) ---
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("댓글 삭제 요청 시 204 No Content를 반환한다")
    void deleteCommentTest() throws Exception {
        mockMvc.perform(delete("/api/comment/1")
                        .with(csrf())) // CSRF 토큰을 포함
                .andExpect(status().isNoContent());
    }
}