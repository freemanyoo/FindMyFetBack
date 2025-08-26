package com.busanit501.findmyfet;

import com.busanit501.findmyfet.domain.FindPetPost;
import com.busanit501.findmyfet.repository.FindPetPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FindPetPostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FindPetPostRepository findPetPostRepository;

    private FindPetPost post1;
    private FindPetPost post2;
    private FindPetPost post3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        post1 = FindPetPost.builder()
                .title("서울 강남구 골든리트리버")
                .content("3살 수컷 골든리트리버입니다.")
                .author("김철수")
                .lostDate(LocalDate.of(2024, 8, 20))
                .cityProvince("서울특별시")
                .district("강남구")
                .animalType(FindPetPost.AnimalType.DOG)
                .breed("골든리트리버")
                .gender(FindPetPost.Gender.MALE)
                .isFound(false)
                .contact("010-1234-5678")
                .build();

        post2 = FindPetPost.builder()
                .title("부산 해운대구 페르시안 고양이")
                .content("2살 암컷 페르시안 고양이입니다.")
                .author("이영희")
                .lostDate(LocalDate.of(2024, 8, 21))
                .cityProvince("부산광역시")
                .district("해운대구")
                .animalType(FindPetPost.AnimalType.CAT)
                .breed("페르시안")
                .gender(FindPetPost.Gender.FEMALE)
                .isFound(false)
                .contact("010-9876-5432")
                .build();

        post3 = FindPetPost.builder()
                .title("서울 서초구 비글")
                .content("1살 암컷 비글입니다.")
                .author("박민수")
                .lostDate(LocalDate.of(2024, 8, 15))
                .cityProvince("서울특별시")
                .district("서초구")
                .animalType(FindPetPost.AnimalType.DOG)
                .breed("비글")
                .gender(FindPetPost.Gender.FEMALE)
                .isFound(true) // 찾은 상태
                .contact("010-5555-7777")
                .build();

        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);
        entityManager.persistAndFlush(post3);
    }

    @Test
    @DisplayName("중복 제거된 시·도 목록 조회")
    void findDistinctCityProvinces_Success() {
        // when
        List<String> result = findPetPostRepository.findDistinctCityProvinces();

        // then
        assertThat(result).contains("서울특별시", "부산광역시");
        assertThat(result).hasSize(2);

        // 정렬되어 있는지 확인
        assertThat(result).isSorted();
    }

    @Test
    @DisplayName("특정 시·도의 군/구 목록 조회")
    void findDistinctDistrictsByCityProvince_Success() {
        // when
        List<String> result = findPetPostRepository
                .findDistinctDistrictsByCityProvince("서울특별시");

        // then
        assertThat(result).contains("강남구", "서초구");
        assertThat(result).hasSize(2);

        // 정렬되어 있는지 확인
        assertThat(result).isSorted();
    }

    @Test
    @DisplayName("존재하지 않는 시·도의 군/구 목록 조회")
    void findDistinctDistrictsByCityProvince_NotFound_ReturnsEmpty() {
        // when
        List<String> result = findPetPostRepository
                .findDistinctDistrictsByCityProvince("존재하지않는시");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 동물 타입의 품종 목록 조회")
    void findDistinctBreedsByAnimalType_Success() {
        // when
        List<String> result = findPetPostRepository
                .findDistinctBreedsByAnimalType(FindPetPost.AnimalType.DOG);

        // then
        assertThat(result).contains("골든리트리버", "비글");
        assertThat(result).hasSize(2);

        // 정렬되어 있는지 확인
        assertThat(result).isSorted();
    }

    @Test
    @DisplayName("고양이 타입의 품종 목록 조회")
    void findDistinctBreedsByAnimalType_Cat_Success() {
        // when
        List<String> result = findPetPostRepository
                .findDistinctBreedsByAnimalType(FindPetPost.AnimalType.CAT);

        // then
        assertThat(result).contains("페르시안");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 동물 타입의 품종 목록 조회")
    void findDistinctBreedsByAnimalType_NotFound_ReturnsEmpty() {
        // when
        List<String> result = findPetPostRepository
                .findDistinctBreedsByAnimalType(FindPetPost.AnimalType.OTHER);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("검색 완료되지 않은 게시글 조회")
    void findByIsFoundFalseOrderByCreatedAtDesc_Success() {
        // when
        List<FindPetPost> result = findPetPostRepository
                .findByIsFoundFalseOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(post -> !post.getIsFound());

        // 생성일시 역순으로 정렬되어 있는지 확인
        assertThat(result.get(0).getCreatedAt())
                .isAfterOrEqualTo(result.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("작성자 이름으로 게시글 검색 - 부분 일치")
    void findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc_Success() {
        // when
        List<FindPetPost> result = findPetPostRepository
                .findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc("김");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor()).contains("김");
        assertThat(result.get(0).getAuthor()).isEqualTo("김철수");
    }

    @Test
    @DisplayName("작성자 이름으로 게시글 검색 - 대소문자 무시")
    void findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc_IgnoreCase() {
        // when
        List<FindPetPost> result = findPetPostRepository
                .findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc("이영");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor()).isEqualTo("이영희");
    }

    @Test
    @DisplayName("존재하지 않는 작성자로 검색")
    void findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc_NotFound() {
        // when
        List<FindPetPost> result = findPetPostRepository
                .findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc("존재하지않는작성자");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 지역의 게시글 수 조회")
    void countByCityProvinceAndDistrict_Success() {
        // when
        long count = findPetPostRepository
                .countByCityProvinceAndDistrict("서울특별시", "강남구");

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 지역의 게시글 수 조회")
    void countByCityProvinceAndDistrict_NotFound_ReturnsZero() {
        // when
        long count = findPetPostRepository
                .countByCityProvinceAndDistrict("존재하지않는시", "존재하지않는구");

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글 저장 및 조회 테스트")
    void saveAndFindById_Success() {
        // given
        FindPetPost newPost = FindPetPost.builder()
                .title("새로운 게시글")
                .content("새로운 내용")
                .author("새로운 작성자")
                .lostDate(LocalDate.now())
                .cityProvince("인천광역시")
                .district("연수구")
                .animalType(FindPetPost.AnimalType.OTHER)
                .breed("토끼")
                .gender(FindPetPost.Gender.UNKNOWN)
                .isFound(false)
                .contact("010-0000-0000")
                .build();

        // when
        FindPetPost savedPost = findPetPostRepository.save(newPost);
        FindPetPost foundPost = findPetPostRepository.findById(savedPost.getId()).orElse(null);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo("새로운 게시글");
        assertThat(foundPost.getCreatedAt()).isNotNull();
        assertThat(foundPost.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글 업데이트 테스트")
    void updatePost_Success() {
        // given
        FindPetPost existingPost = findPetPostRepository.findById(post1.getId()).orElse(null);
        assertThat(existingPost).isNotNull();

        // when
        existingPost.setIsFound(true);
        existingPost.setTitle("업데이트된 제목");
        FindPetPost updatedPost = findPetPostRepository.save(existingPost);

        // then
        assertThat(updatedPost.getIsFound()).isTrue();
        assertThat(updatedPost.getTitle()).isEqualTo("업데이트된 제목");
        assertThat(updatedPost.getUpdatedAt()).isAfter(updatedPost.getCreatedAt());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePost_Success() {
        // given
        Long postId = post1.getId();
        assertThat(findPetPostRepository.existsById(postId)).isTrue();

        // when
        findPetPostRepository.deleteById(postId);

        // then
        assertThat(findPetPostRepository.existsById(postId)).isFalse();
        assertThat(findPetPostRepository.findById(postId)).isEmpty();
    }

    @Test
    @DisplayName("전체 게시글 수 조회")
    void countAllPosts_Success() {
        // when
        long totalCount = findPetPostRepository.count();

        // then
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    @DisplayName("null 값이 포함된 데이터 처리")
    void handleNullValues_Success() {
        // given
        FindPetPost postWithNulls = FindPetPost.builder()
                .title("필수 필드만 있는 게시글")
                .author("테스트 작성자")
                // 다른 필드들은 null로 남겨둠
                .build();

        // when
        FindPetPost savedPost = findPetPostRepository.save(postWithNulls);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("필수 필드만 있는 게시글");
        assertThat(savedPost.getContent()).isNull();
        assertThat(savedPost.getLostDate()).isNull();
    }}