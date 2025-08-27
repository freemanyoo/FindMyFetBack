package com.busanit501.findmyfet.config;

import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.PostType;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.repository.post.PostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // 데이터 삽입은 트랜잭션 내에서 이루어져야 함
    public void run(String... args) throws Exception {
        log.info("데이터 시딩 시작...");

        // 게시글이 이미 존재하면 시딩하지 않음
        if (postRepository.count() > 0) {
            log.info("이미 데이터가 존재하여 시딩을 건너뜁니다.");
            return;
        }

        // 테스트용 사용자 생성 또는 조회
        Optional<User> existingUser = userRepository.findByEmail("seeder@example.com");
        User seederUser;

        if (existingUser.isPresent()) {
            seederUser = existingUser.get();
            log.info("기존 시딩 사용자 발견: {}", seederUser.getEmail());
        } else {
            seederUser = User.builder()
                    .email("seeder@example.com")
                    .password(passwordEncoder.encode("seeder123!"))
                    .name("DataSeeder")
                    .loginId("dataseeder")
                    .phoneNumber("010-9999-8888")
                    .address("서울시 강남구")
                    .build();
            userRepository.save(seederUser);
            log.info("새로운 시딩 사용자 생성: {}", seederUser.getEmail());
        }

        // 더미 게시글 25개 생성
        for (int i = 1; i <= 25; i++) {
            Post post = Post.builder()
                    .title("시딩 게시글 " + i + " - " + (i % 2 == 0 ? "실종" : "보호"))
                    .content("이것은 데이터 시딩을 통해 생성된 게시글 내용입니다. 번호: " + i)
                    .animalName(i % 3 == 0 ? "강아지" : (i % 3 == 1 ? "고양이" : "기타"))
                    .animalAge(i % 5 + 1)
                    .animalCategory(i % 3 == 0 ? "개" : (i % 3 == 1 ? "고양이" : "새"))
                    .animalBreed(i % 4 == 0 ? "진돗개" : (i % 4 == 1 ? "페르시안" : (i % 4 == 2 ? "앵무새" : "믹스")))
                    .lostTime(LocalDateTime.now().minusDays(25 - i))
                    .latitude(35.10000 + i * 0.005)
                    .longitude(129.00000 + i * 0.005)
                    .location(i % 2 == 0 ? "부산 해운대구" : "서울 마포구")
                    .postType(i % 2 == 0 ? PostType.MISSING : PostType.SHELTER)
                    .status(Status.ACTIVE)
                    .user(seederUser)
                    .build();
            postRepository.save(post);
        }
        log.info("데이터 시딩 완료. {}개의 게시글이 추가되었습니다.", postRepository.count());
    }
}
