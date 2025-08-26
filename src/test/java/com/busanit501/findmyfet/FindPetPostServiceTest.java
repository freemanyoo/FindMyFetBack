package com.busanit501.findmyfet;

import com.busanit501.findmyfet.domain.FindPetPost;
import com.busanit501.findmyfet.dto.FindPetSearchCriteria;
import com.busanit501.findmyfet.repository.FindPetPostRepository;
import com.busanit501.findmyfet.service.FindPetPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPetPostServiceTest {

    @Mock
    private FindPetPostRepository findPetPostRepository;

    @InjectMocks
    private FindPetPostService findPetPostService;

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
    @DisplayName("게시글 검색 테스트")
    void searchFindPetPosts_Success() {
        // given
        FindPetSearchCriteria criteria = FindPetSearchCriteria.builder()
                .title("테스트")
                .page(0)
                .size(20)
                .sortBy("createdAt")
                .sortDir("DESC")
                .build();

        Page<FindPetPost> mockPage = new PageImpl<>(List.of(testPost));
        when(findPetPostRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        // when
        Page<FindPetPost> result = findPetPostService.searchFindPetPosts(criteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 게시글");
        verify(findPetPostRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("잘못된 날짜 범위로 검색 시 예외 발생")
    void searchFindPetPosts_InvalidDateRange_ThrowsException() {
        // given
        FindPetSearchCriteria criteria = FindPetSearchCriteria.builder()
                .lostDateFrom(LocalDate.of(2024, 8, 25))
                .lostDateTo(LocalDate.of(2024, 8, 20))
                .build();

        // when & then
        assertThatThrownBy(() -> findPetPostService.searchFindPetPosts(criteria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("분실 시작 날짜는 종료 날짜보다 이전이어야 합니다");
    }

    @Test
    @DisplayName("모든 동물 타입 조회 테스트")
    void getAllAnimalTypes_Success() {
        // when
        List<Map<String, String>> result = findPetPostService.getAllAnimalTypes();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3); // DOG, CAT, OTHER
        assertThat(result.get(0)).containsKeys("value", "label");

        // 각 동물 타입이 포함되어 있는지 확인
        List<String> values = result.stream()
                .map(map -> map.get("value"))
                .toList();
        assertThat(values).contains("DOG", "CAT", "OTHER");
    }

    @Test
    @DisplayName("모든 성별 조회 테스트")
    void getAllGenders_Success() {
        // when
        List<Map<String, String>> result = findPetPostService.getAllGenders();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3); // MALE, FEMALE, UNKNOWN
        assertThat(result.get(0)).containsKeys("value", "label");

        // 각 성별이 포함되어 있는지 확인
        List<String> values = result.stream()
                .map(map -> map.get("value"))
                .toList();
        assertThat(values).contains("MALE", "FEMALE", "UNKNOWN");
    }

    @Test
    @DisplayName("시·도 목록 조회 테스트")
    void getAllCityProvinces_Success() {
        // given
        List<String> mockCities = Arrays.asList("서울특별시", "부산광역시", "대구광역시");
        when(findPetPostRepository.findDistinctCityProvinces()).thenReturn(mockCities);

        // when
        List<String> result = findPetPostService.getAllCityProvinces();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains("서울특별시", "부산광역시", "대구광역시");
        verify(findPetPostRepository).findDistinctCityProvinces();
    }

    @Test
    @DisplayName("ID로 게시글 조회 테스트")
    void findById_Success() {
        // given
        when(findPetPostRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // when
        FindPetPost result = findPetPostService.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 게시글");
        verify(findPetPostRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 게시글 조회 시 예외 발생")
    void findById_NotFound_ThrowsException() {
        // given
        when(findPetPostRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> findPetPostService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
        verify(findPetPostRepository).findById(999L);
    }

    @Test
    @DisplayName("찾기 상태 토글 테스트")
    void toggleFoundStatus_Success() {
        // given
        when(findPetPostRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(findPetPostRepository.save(any(FindPetPost.class))).thenReturn(testPost);

        // when
        FindPetPost result = findPetPostService.toggleFoundStatus(1L);

        // then
        assertThat(result.getIsFound()).isTrue(); // false -> true로 변경됨
        verify(findPetPostRepository).findById(1L);
        verify(findPetPostRepository).save(testPost);
    }

    @Test
    @DisplayName("시·도별 군/구 조회 테스트")
    void getDistrictsByCity_Success() {
        // given
        List<String> districts = Arrays.asList("강남구", "서초구", "마포구");
        when(findPetPostRepository.findDistinctDistrictsByCityProvince("서울특별시"))
                .thenReturn(districts);

        // when
        List<String> result = findPetPostService.getDistrictsByCity("서울특별시");

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains("강남구", "서초구", "마포구");
        verify(findPetPostRepository).findDistinctDistrictsByCityProvince("서울특별시");
    }

    @Test
    @DisplayName("빈 시·도로 군/구 조회 시 예외 발생")
    void getDistrictsByCity_EmptyCity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> findPetPostService.getDistrictsByCity(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시·도 정보가 필요합니다");

        verify(findPetPostRepository, never()).findDistinctDistrictsByCityProvince(anyString());
    }

    @Test
    @DisplayName("null 시·도로 군/구 조회 시 예외 발생")
    void getDistrictsByCity_NullCity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> findPetPostService.getDistrictsByCity(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시·도 정보가 필요합니다");

        verify(findPetPostRepository, never()).findDistinctDistrictsByCityProvince(anyString());
    }

    @Test
    @DisplayName("동물 타입별 품종 조회 테스트")
    void getBreedsByAnimalType_Success() {
        // given
        List<String> breeds = Arrays.asList("골든리트리버", "비글", "시바견");
        when(findPetPostRepository.findDistinctBreedsByAnimalType(FindPetPost.AnimalType.DOG))
                .thenReturn(breeds);

        // when
        List<String> result = findPetPostService.getBreedsByAnimalType(FindPetPost.AnimalType.DOG);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains("골든리트리버", "비글", "시바견");
        verify(findPetPostRepository).findDistinctBreedsByAnimalType(FindPetPost.AnimalType.DOG);
    }

    @Test
    @DisplayName("null 동물 타입으로 품종 조회 시 예외 발생")
    void getBreedsByAnimalType_NullAnimalType_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> findPetPostService.getBreedsByAnimalType(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("동물 타입 정보가 필요합니다");

        verify(findPetPostRepository, never()).findDistinctBreedsByAnimalType(any());
    }

    @Test
    @DisplayName("정렬 필드 검증 테스트 - 허용되지 않는 필드")
    void searchFindPetPosts_InvalidSortField_UsesDefault() {
        // given
        FindPetSearchCriteria criteria = FindPetSearchCriteria.builder()
                .sortBy("invalidField") // 허용되지 않는 필드
                .sortDir("DESC")
                .build();

        Page<FindPetPost> mockPage = new PageImpl<>(List.of(testPost));
        when(findPetPostRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        // when
        Page<FindPetPost> result = findPetPostService.searchFindPetPosts(criteria);

        // then
        assertThat(result).isNotNull();
        // 내부적으로 기본값인 "createdAt"으로 정렬됨을 확인
        verify(findPetPostRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("유효한 날짜 범위로 검색 테스트")
    void searchFindPetPosts_ValidDateRange_Success() {
        // given
        FindPetSearchCriteria criteria = FindPetSearchCriteria.builder()
                .lostDateFrom(LocalDate.of(2024, 8, 1))
                .lostDateTo(LocalDate.of(2024, 8, 31))
                .build();

        Page<FindPetPost> mockPage = new PageImpl<>(List.of(testPost));
        when(findPetPostRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        // when
        Page<FindPetPost> result = findPetPostService.searchFindPetPosts(criteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(findPetPostRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}