package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.repository.UserRepository;
import com.busanit501.findmyfet.security.JwtUtil;
import com.busanit501.findmyfet.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTests {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    private CommentDTO mockCommentDtoWithImage;
    private CommentDTO mockCommentDtoWithoutImage;

    @BeforeEach
    void setUp() {
        mockCommentDtoWithImage = CommentDTO.builder()
                .commentId(1L)
                .postId(1L)
                .userId(1L)
                .content("테스트 댓글입니다.")
                .imageUrl("test_image.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockCommentDtoWithoutImage = CommentDTO.builder()
                .commentId(2L)
                .postId(1L)
                .userId(1L)
                .content("이미지 없는 댓글입니다.")
                .imageUrl(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("게시글 댓글 목록 조회 테스트")
    @WithMockUser
    public void getCommentListTest() throws Exception {
        Long postId = 1L;
        List<CommentDTO> commentList = Collections.singletonList(mockCommentDtoWithImage);
        given(commentService.getCommentsByPostId(postId)).willReturn(commentList);

        mockMvc.perform(get("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(mockCommentDtoWithImage.getContent()));
    }

    @Test
    @DisplayName("✅ 이미지와 텍스트를 포함한 댓글 작성 테스트")
    public void createCommentWithImageTest() throws Exception {
        Long postId = 1L;
        String loginId = "testUser";

        String commentDtoString = objectMapper.writeValueAsString(mockCommentDtoWithImage);
        MockMultipartFile commentDtoPart = new MockMultipartFile("commentDTO", "", MediaType.APPLICATION_JSON_VALUE, commentDtoString.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile imageFilePart = new MockMultipartFile("imageFile", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        given(commentService.createComment(any(CommentDTO.class), eq(loginId), any(MultipartFile.class)))
                .willReturn(mockCommentDtoWithImage);

        mockMvc.perform(multipart("/api/posts/{postId}/comments", postId)
                        .file(commentDtoPart)
                        .file(imageFilePart)
                        .with(csrf())
                        .with(user("testUser"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(mockCommentDtoWithImage.getCommentId()))
                .andExpect(jsonPath("$.imageUrl").value(mockCommentDtoWithImage.getImageUrl()));
    }

    @Test
    @DisplayName("✅ 텍스트만 있는 (이미지 없는) 댓글 작성 테스트")
    public void createCommentWithoutImageTest() throws Exception {
        Long postId = 1L;
        String loginId = "testUser";

        String commentDtoString = objectMapper.writeValueAsString(mockCommentDtoWithoutImage);
        MockMultipartFile commentDtoPart = new MockMultipartFile("commentDTO", "", MediaType.APPLICATION_JSON_VALUE, commentDtoString.getBytes(StandardCharsets.UTF_8));

        // 서비스의 createComment 메서드가 imageFile 인자로 null을 받아도 잘 동작하는지 테스트
        given(commentService.createComment(any(CommentDTO.class), eq(loginId), eq(null)))
                .willReturn(mockCommentDtoWithoutImage);

        mockMvc.perform(multipart("/api/posts/{postId}/comments", postId)
                        .file(commentDtoPart) // 이미지 파트는 보내지 않음
                        .with(csrf())
                        .with(user("testUser"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(mockCommentDtoWithoutImage.getCommentId()))
                .andExpect(jsonPath("$.imageUrl").isEmpty());
    }

    @Test
    @DisplayName("이미지 포함 댓글 수정 테스트")
    public void updateCommentWithImageTest() throws Exception {
        Long commentId = 1L;
        String loginId = "testUser";

        String commentDtoString = objectMapper.writeValueAsString(mockCommentDtoWithImage);
        MockMultipartFile commentDtoPart = new MockMultipartFile("commentDTO", "", MediaType.APPLICATION_JSON_VALUE, commentDtoString.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile imageFilePart = new MockMultipartFile("imageFile", "updated.jpg", MediaType.IMAGE_JPEG_VALUE, "updated image content".getBytes());

        given(commentService.updateComment(eq(commentId), any(CommentDTO.class), eq(loginId), any(MultipartFile.class)))
                .willReturn(mockCommentDtoWithImage);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/comments/{commentId}", commentId)
                        .file(commentDtoPart)
                        .file(imageFilePart)
                        .with(csrf())
                        .with(user("testUser"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(mockCommentDtoWithImage.getContent()));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    public void deleteCommentTest() throws Exception {
        Long commentId = 1L;
        String loginId = "testUser";
        doNothing().when(commentService).deleteComment(eq(commentId), eq(loginId));

        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                        .with(csrf())
                        .with(user("testUser")))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}