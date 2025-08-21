//package com.example.petfinder.controller;
//
//import com.example.petfinder.entity.LostPetPost;
//import com.example.petfinder.service.LostPetPostService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(com.example.petfinder.controller.LostPetPostController.class)
//public class LostPetPostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private LostPetPostService lostPetPostService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private LostPetPost samplePost;
//    private List<LostPetPost> samplePosts;
//
//    @BeforeEach
//    void setUp() {
//        samplePost = LostPetPost.builder()
//                .id(1L)
//                .title("골든리트리버 찾습니다")
//                .content("강남구에서 분실된 골든리트리버입니다.")
//                .author("김철수")
//                .lostDate(LocalDate.of(2024, 8, 15))
//                .cityProvince("서울특별시")
//                .district("강남구")
//                .animalType(LostPetPost.AnimalType.DOG)
//                .breed("골든리트리버")
//                .gender(LostPetPost.Gender.MALE)
//                .isFound(false)
//                .contact("010-1234-5678")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        samplePosts = Arrays.asList(samplePost);
//    }
//
//    @Test
//    void testSearchLostPets() throws Exception {
//        // Given
//        Page<LostPetPost> pageResult = new PageImpl<>(samplePosts);
//        when(lostPetPostService.searchLostPetPosts(any())).thenReturn(pageResult);
//
//        // When & Then
//        mockMvc.perform(get("/api/lost-pets/search")
//                        .param("title", "골든")
//                        .param("cityProvince", "서울특별시")
//                        .param("animalType", "DOG")
//                        .param("page", "0")
//                        .param("size", "20"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content[0].title").value("골든리트리버 찾습니다"))
//                .andExpect(jsonPath("$.totalElements").value(1))
//                .andExpect(jsonPath("$.totalPages").value(1));
//    }
//
//    @Test
//    void testGetFilterOptions() throws Exception {
//        // Given
//        List<String> cityProvinces = Arrays.asList("서울특별시", "부산광역시", "대구광역시");
//        when(lostPetPostService.getAllCityProvinces()).thenReturn(cityProvinces);
//        when(lostPetPostService.getAllAnimalTypes()).thenReturn(LostPetPost.AnimalType.values());
//        when(lostPetPostService.getAllGenders()).thenReturn(LostPetPost.Gender.values());
//
//        // When & Then
//        mockMvc.perform(get("/api/lost-pets/filter-options"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.cityProvinces").isArray())
//                .andExpected(jsonPath("$.animalTypes").isArray())
//                .andExpected(jsonPath("$.genders").isArray());
//    }
//
//    @Test
//    void testGetDistricts() throws Exception {
//        // Given
//        List<String> districts = Arrays.asList("강남구", "서초구", "마포구");
//        when(lostPetPostService.getDistrictsByCity("서울특별시")).thenReturn(districts);
//
//        // When & Then
//        mockMvc.perform(get("/api/lost-pets/districts")
//                        .param("cityProvince", "서울특별시"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0]").value("강남구"));
//    }
//
//    @Test
//    void testGetBreeds() throws Exception {
//        // Given
//        List<String> breeds = Arrays.asList("골든리트리버", "래브라도", "시바견");
//        when(lostPetPostService.getBreedsByAnimalType(LostPetPost.AnimalType.DOG))
//                .thenReturn(breeds);
//
//        // When & Then
//        mockMvc.perform(get("/api/lost-pets/breeds")
//                        .param("animalType", "DOG"))
//                .andExpect(status().isOk())
//                .andExpected(jsonPath("$").isArray())
//                .andExpected(jsonPath("$[0]").value("골든리트리버"));
//    }
//
//    @Test
//    void testToggleFoundStatus() throws Exception {
//        // Given
//        LostPetPost updatedPost = samplePost;
//        updatedPost.setIsFound(true);
//        when(lostPetPostService.toggleFoundStatus(1L)).thenReturn(updatedPost);
//
//        // When & Then
//        mockMvc.perform(patch("/api/lost-pets/1/toggle-found"))
//                .andExpect(status().isOk())
//                .andExpected(jsonPath("$.id").value(1))
//                .andExpected(jsonPath("$.isFound").value(true))
//                .andExpected(jsonPath("$.message").exists());
//    }
//
//    @Test
//    void testSearchWithInvalidParameters() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/lost-pets/search")
//                        .param("page", "-1")  // 잘못된 페이지 번호
//                        .param("size", "0"))  // 잘못된 페이지 크기
//                .andExpect(status().isOk()); // 서비스에서 기본값으로 처리됨
//    }
//
//    @Test
//    void testGetDistrictsWithEmptyCity() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/lost-pets/districts")
//                        .param("cityProvince", ""))
//                .andExpect(status().isBadRequest());
//    }
//}
//
//// 서비스 레이어 테스트
//package com.example.petfinder.service;
//
//import com.example.petfinder.dto.LostPetSearchCriteria;
//import com.example.petfinder.entity.LostPetPost;
//import com.example.petfinder.repository.LostPetPostRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class LostPetPostServiceTest {
//
//    @Mock
//    private com.example.petfinder.repository.LostPetPostRepository repository;
//
//    @InjectMocks
//    private LostPetPostService service;
//
//    private LostPetPost samplePost;
//
//    @BeforeEach
//    void setUp() {
//        samplePost = LostPetPost.builder()
//                .id(1L)
//                .title("골든리트리버 찾습니다")
//                .author("김철수")
//                .lostDate(LocalDate.of(2024, 8, 15))
//                .cityProvince("서울특별시")
//                .district("강남구")
//                .animalType(LostPetPost.AnimalType.DOG)
//                .breed("골든리트리버")
//                .gender(LostPetPost.Gender.MALE)
//                .isFound(false)
//                .build();
//    }
//
//    @Test
//    void testSearchLostPetPosts() {
//        // Given
//        com.example.petfinder.dto.LostPetSearchCriteria criteria = com.example.petfinder.dto.LostPetSearchCriteria.builder()
//                .title("골든")
//                .cityProvince("서울특별시")
//                .animalType(LostPetPost.AnimalType.DOG)
//                .build();
//
//        Page<LostPetPost> expectedPage = new PageImpl<>(Arrays.asList(samplePost));
//        when(repository.findAll(any(Specification.class), any(Pageable.class)))
//                .thenReturn(expectedPage);
//
//        // When
//        Page<LostPetPost> result = service.searchLostPetPosts(criteria);
//
//        // Then
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getTitle()).contains("골든리트리버");
//    }
//
//    @Test
//    void testFindById() {
//        // Given
//        when(repository.findById(1L)).thenReturn(Optional.of(samplePost));
//
//        // When
//        LostPetPost result = service.findById(1L);
//
//        // Then
//        assertThat(result.getId()).isEqualTo(1L);
//        assertThat(result.getTitle()).isEqualTo("골든리트리버 찾습니다");
//    }
//
//    @Test
//    void testFindByIdNotFound() {
//        // Given
//        when(repository.findById(999L)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> service.findById(999L))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("게시글을 찾을 수 없습니다");
//    }
//
//    @Test
//    void testToggleFoundStatus() {
//        // Given
//        when(repository.findById(1L)).thenReturn(Optional.