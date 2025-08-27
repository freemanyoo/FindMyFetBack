package com.busanit501.findmyfet.data;

import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.PostType;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.repository.post.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
class PostDummyDataSpringBootTest {

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager em;

    // ⚠️ 반드시 실제로 존재하는 유저 ID로 바꿔줘야 함
    private User refUser() {
        Long existingUserId = 1L; // ← 너의 환경에 맞게 수정
        return em.getReference(User.class, existingUserId);
    }

    @Test
    @Transactional
    @Commit // 테스트 트랜잭션을 롤백하지 않고 커밋
    void insertOneSample() {
        User user = refUser();

        Post post = Post.builder()
                .title("【더미】실종 제보 - 콩이")
                .content("을지로입구역 인근에서 잃어버렸어요. 보신 분 제보 부탁드립니다.")
                .animalName("콩이")
                .animalAge(3)
                .animalCategory("개")
                .animalBreed("믹스")
                .lostTime(LocalDateTime.now().minusHours(5))
                .latitude(37.5665)
                .longitude(126.9780)
                .location("서울 중구 을지로입구역")
                .postType(PostType.MISSING)
                .status(Status.ACTIVE)
                .user(user)
                .build();

        postRepository.save(post);
    }

    @Test
    @Transactional
    @Commit // 테스트 트랜잭션을 롤백하지 않고 커밋
    void bulkInsert50() {
        User user = refUser();

        double baseLat = 37.5665;   // 서울 시청 근처
        double baseLng = 126.9780;
        ThreadLocalRandom r = ThreadLocalRandom.current();

        List<Post> batch = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            double lat = baseLat + r.nextDouble(-0.03, 0.03);
            double lng = baseLng + r.nextDouble(-0.03, 0.03);
            PostType type = (i % 3 == 0) ? PostType.SHELTER : PostType.MISSING;

            Post p = Post.builder()
                    .title(String.format("【더미】%s 제보 #%d",
                            (type == PostType.MISSING ? "실종" : "보호소"), i))
                    .content("테스트 더미 데이터입니다. 지도/필터 테스트용.")
                    .animalName((i % 2 == 0) ? "콩이" : "나비")
                    .animalAge(r.nextInt(1, 16))
                    .animalCategory((i % 2 == 0) ? "개" : "고양이")
                    .animalBreed((i % 2 == 0) ? "믹스" : "코리안숏헤어")
                    .lostTime(LocalDateTime.now().minusHours(r.nextInt(1, 72)))
                    .latitude(lat)
                    .longitude(lng)
                    .location("서울 테스트 구역 " + i)
                    .postType(type)
                    .status(Status.ACTIVE)
                    .user(user)
                    .build();

            batch.add(p);
        }

        postRepository.saveAll(batch);
    }
}
