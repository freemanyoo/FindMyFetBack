package com.busanit501.findmyfet;

import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.PostType;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.repository.post.PostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SearchTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 기존 데이터 삭제 (테스트 환경에 따라 필요 없을 수도 있음)
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("testuser@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("TestUser") // nickname 대신 name 사용
                .loginId("testuser") // loginId 추가
                .phoneNumber("010-1234-5678") // phoneNumber 추가
                .address("부산시") // address 추가
                .build();
        userRepository.save(testUser);

        // 더미 게시글 5개 생성
        for (int i = 1; i <= 5; i++) {
            Post post = Post.builder()
                    .title("테스트 게시글 " + i)
                    .content("이것은 테스트 게시글 내용입니다 " + i)
                    .animalName("테스트 동물 " + i)
                    .animalAge(i)
                    .animalCategory(i % 2 == 0 ? "개" : "고양이")
                    .animalBreed(i % 2 == 0 ? "푸들" : "코숏")
                    .lostTime(LocalDateTime.now().minusDays(i))
                    .latitude(35.12345 + i * 0.001)
                    .longitude(129.01234 + i * 0.001)
                    .location("테스트 장소 " + i)
                    .postType(PostType.MISSING)
                    .status(Status.ACTIVE)
                    .user(testUser) // 사용자 연결
                    .build();
            postRepository.save(post);
        }
    }

    @Test
    @DisplayName("더미 게시글 5개가 성공적으로 생성되는지 확인")
    void testDummyPostsCreation() {
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(5);
        posts.forEach(post -> System.out.println("Created Post: " + post.getTitle() + " by " + post.getUser().getName()));
    }
}