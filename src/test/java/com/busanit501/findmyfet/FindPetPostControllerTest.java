package com.busanit501.findmyfet;

import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.repository.FindPetPostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
public class FindPetPostControllerTest {

    private FindPetPostRepository findPetPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성 또는 조회
                testUser = userRepository.findByLoginId("testuser").orElseGet(() -> {
            User user = User.builder()
                    .loginId("testuser")
                    .password("password123")
                    .name("테스트유저")
                    .phoneNumber("010-1234-5678")
                    .email("test@example.com")
                    .address("테스트 주소")
                    .build();
            User savedUser = userRepository.save(user);
            // 영속성 컨텍스트에 즉시 반영
            entityManager.flush();
            // 영속성 컨텍스트 초기화 (테스트 메서드에서 새로운 트랜잭션으로 로드되도록)
            entityManager.clear();
            return savedUser;
        });
        // 초기화 후 다시 로드하여 영속성 컨텍스트에 연결된 상태로 만듦
        testUser = userRepository.findByLoginId("testuser").get();
    }

    @Test
    @DisplayName("더미 FindPetPost 5개 생성")
    void createDummyFindPetPosts() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            FindPetPost findPetPost = FindPetPost.builder()
                    .user(testUser) // 위에서 생성한 테스트 사용자 연결
                    .title("더미 게시글 제목 " + i)
                    .content("더미 게시글 내용 " + i + "입니다. 이것은 테스트용 게시글입니다.")
                    .animalType(FindPetPost.AnimalType.DOG)
                    .breed("믹스견")
                    .gender(FindPetPost.Gender.MALE)
                    .lostDate(LocalDate.of(2024, 8, i))
                    .cityProvince("서울시")
                    .district("강남구")
                    .contact("010-9876-5432")
                    .isFound(false)
                    .build();
            findPetPostRepository.save(findPetPost);
        });

        System.out.println("더미 FindPetPost 5개가 성공적으로 생성되었습니다.");
    }
}
