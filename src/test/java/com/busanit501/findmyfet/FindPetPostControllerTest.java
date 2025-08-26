package com.busanit501.findmyfet.config;

import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.repository.PostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DummyDataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 더미 데이터가 이미 존재하는지 확인
        if (postRepository.count() > 0) {
            log.info("더미 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("더미 데이터 생성을 시작합니다.");

        // 테스트 사용자 생성
        User testUser = createTestUser();

        // 더미 게시글 5개 생성
        createDummyPosts(testUser);

        log.info("더미 데이터 생성이 완료되었습니다.");
    }

    private User createTestUser() {
        // User 엔티티의 실제 필드에 맞게 수정 필요
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .build();

        return userRepository.save(user);
    }

    private void createDummyPosts(User user) {
        // 더미 게시글 1 - 강아지 실종
        Post post1 = Post.builder()
                .title("골든리트리버 '바둑이' 실종신고")
                .content("4월 15일 오후 3시경 부산 해운대구 우동에서 실종되었습니다. 목줄을 하고 있었고, 사람을 잘 따라갑니다. 발견하시면 연락 부탁드립니다.")
                .animalName("바둑이")
                .animalAge(3)
                .animalCategory("개")
                .animalBreed("골든리트리버")
                .lostTime(LocalDateTime.of(2024, 4, 15, 15, 0))
                .latitude(35.1631)
                .longitude(129.1635)
                .location("부산광역시 해운대구 우동")
                .postType(Post.PostType.MISSING)
                .status(Post.Status.ACTIVE)
                .user(user)
                .build();

        // 더미 게시글 2 - 고양이 보호중
        Post post2 = Post.builder()
                .title("코숏 고양이 임시보호 중입니다")
                .content("4월 20일 서면 지하철역 근처에서 발견한 고양이입니다. 주인을 찾고 있습니다. 매우 온순하고 건강상태는 양호합니다.")
                .animalName("미상")
                .animalAge(1)
                .animalCategory("고양이")
                .animalBreed("코리안숏헤어")
                .lostTime(LocalDateTime.of(2024, 4, 20, 10, 30))
                .latitude(35.1578)
                .longitude(129.0594)
                .location("부산광역시 부산진구 서면")
                .postType(Post.PostType.SHELTER)
                .status(Post.Status.ACTIVE)
                .user(user)
                .build();

        // 더미 게시글 3 - 완료된 사례
        Post post3 = Post.builder()
                .title("비글 '초코' 찾았습니다!")
                .content("실종신고 올렸던 비글 초코를 찾았습니다. 도움 주신 모든 분들께 감사드립니다.")
                .animalName("초코")
                .animalAge(5)
                .animalCategory("개")
                .animalBreed("비글")
                .lostTime(LocalDateTime.of(2024, 4, 10, 14, 20))
                .latitude(35.1796)
                .longitude(129.0756)
                .location("부산광역시 동래구 온천동")
                .postType(Post.PostType.MISSING)
                .status(Post.Status.COMPLETED)
                .user(user)
                .build();

        // 더미 게시글 4 - 믹스견 실종
        Post post4 = Post.builder()
                .title("흰색 믹스견 '구름이' 실종")
                .content("4월 18일 오전 산책 중 놀라서 도망갔습니다. 흰색에 갈색 반점이 있는 중형견입니다. 매우 겁이 많아서 사람이 다가가면 도망갈 수 있습니다.")
                .animalName("구름이")
                .animalAge(2)
                .animalCategory("개")
                .animalBreed("믹스견")
                .lostTime(LocalDateTime.of(2024, 4, 18, 8, 45))
                .latitude(35.1104)
                .longitude(129.0431)
                .location("부산광역시 영도구 남항동")
                .postType(Post.PostType.MISSING)
                .status(Post.Status.ACTIVE)
                .user(user)
                .build();

        // 더미 게시글 5 - 페르시안 고양이 보호
        Post post5 = Post.builder()
                .title("페르시안 고양이 보호소에서 주인을 찾습니다")
                .content("4월 22일 광안리 해변가에서 발견된 페르시안 고양이입니다. 털이 길고 회색빛이 도는 흰색입니다. 건강검진 후 임시보호 중입니다.")
                .animalName("미상")
                .animalAge(4)
                .animalCategory("고양이")
                .animalBreed("페르시안")
                .lostTime(LocalDateTime.of(2024, 4, 22, 16, 15))
                .latitude(35.1534)
                .longitude(129.1186)
                .location("부산광역시 수영구 광안동")
                .postType(Post.PostType.SHELTER)
                .status(Post.Status.ACTIVE)
                .user(user)
                .build();

        // 모든 게시글 저장
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);

        log.info("5개의 더미 게시글이 생성되었습니다.");
    }
}