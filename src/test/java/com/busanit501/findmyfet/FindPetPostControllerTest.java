package com.busanit501.findmyfet;

import com.busanit501.findmyfet.controller.FindPetPostController;
import com.busanit501.findmyfet.domain.FindPetPost;
import com.busanit501.findmyfet.dto.FindPetSearchCriteria;
import com.busanit501.findmyfet.service.FindPetPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FindPetPostController.class)
class FindPetPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindPetPostService findPetPostService;

    @Autowired
    private ObjectMapper objectMapper;

    private FindPetPost testPost;

    @BeforeEach
    void setUp() {
        testPost = FindPetPost.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .author("테스트 작성자")
                .lostDate(LocalDate.of(2024, 8, 20))
                .cityProvince("서울특별시")
                .district("강남구")
                .animalType(FindPetPost.AnimalType.DOG)
                .breed("골든리트리버")
                .gender(FindPetPost.Gender.MALE)
                .isFound(false)
                .contact("010-1234-5678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("검색 API 테스트 - 성공")
    void searchFindPets_Success() throws Exception {
        // given
        Page<FindPetPost> mockPage = new PageImpl<>(List.of(testPost));
        when(findPetPostService.searchFindPetPosts(any(FindPetSearchCriteria.class)))
                .thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/find-pets/search")
                        .param("title", "테스트")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDir", "DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].title").value("테스트 게시글"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("필터 옵션 조회 API 테스트")
    void getFilterOptions_Success() throws Exception {
        // given
        Map<String, String> animalType = new HashMap<>();
        animalType.put("value", "DOG");
        animalType.put("label", "개");

        Map<String, String> gender = new HashMap<>();
        gender.put("value", "MALE");
        gender.put("label", "수컷");

        when(findPetPostService.getAllAnimalTypes()).thenReturn(List.of(animalType));
        when(findPetPostService.getAllGenders()).thenReturn(List.of(gender));
        when(findPetPostService.getAllCityProvinces()).thenReturn(List.of("서울특별시"));

        // when & then
        mockMvc.perform(get("/api/find-pets/filter-options")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalTypes").exists())
                .andExpect(jsonPath("$.genders").exists())
                .andExpect(jsonPath("$.cityProvinces").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("특정 게시글 조회 API 테스트")
    void getFindPetPost_Success() throws Exception {
        // given
        when(findPetPostService.findById(1L)).thenReturn(testPost);

        // when & then
        mockMvc.perform(get("/api/find-pets/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.author").value("테스트 작성자"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 찾기 상태 토글 API 테스트")
    void toggleFoundStatus_Success() throws Exception {
        // given
        FindPetPost updatedPost = FindPetPost.builder()
                .id(1L)
                .title("테스트 게시글")
                .isFound(true)
                .build();

        when(findPetPostService.toggleFoundStatus(1L)).thenReturn(updatedPost);

        // when & then
        mockMvc.perform(patch("/api/find-pets/1/toggle-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isFound").value(true))
                .andExpect(jsonPath("$.message").value("검색이 완료되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("지역별 군/구 조회 API 테스트")
    void getDistricts_Success() throws Exception {
        // given
        when(findPetPostService.getDistrictsByCity("서울특별시"))
                .thenReturn(Arrays.asList("강남구", "서초구", "마포구"));

        // when & then
        mockMvc.perform(get("/api/find-pets/districts")
                        .param("cityProvince", "서울특별시")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("강남구"))
                .andDo(print());
    }

    @Test
    @DisplayName("동물 타입별 품종 조회 API 테스트")
    void getBreeds_Success() throws Exception {
        // given
        when(findPetPostService.getBreedsByAnimalType(FindPetPost.AnimalType.DOG))
                .thenReturn(Arrays.asList("골든리트리버", "비글", "시바견"));

        // when & then
        mockMvc.perform(get("/api/find-pets/breeds")
                        .param("animalType", "DOG")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("골든리트리버"))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 404 반환")
    void getFindPetPost_NotFound_Returns404() throws Exception {
        // given
        when(findPetPostService.findById(999L))
                .thenThrow(new RuntimeException("게시글을 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(get("/api/find-pets/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 파라미터로 군/구 조회 시 400 반환")
    void getDistricts_InvalidParameter_Returns400() throws Exception {
        // given
        when(findPetPostService.getDistrictsByCity(""))
                .thenThrow(new IllegalArgumentException("시·도 정보가 필요합니다"));

        // when & then
        mockMvc.perform(get("/api/find-pets/districts")
                        .param("cityProvince", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}