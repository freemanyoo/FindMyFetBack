import com.busanit501.findmyfet.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    /**
     * 모든 지역(location) 목록을 중복 제거하여 조회
     */
    @Query("SELECT DISTINCT p.location FROM Post p WHERE p.location IS NOT NULL ORDER BY p.location")
    List<String> findDistinctLocations();

    /**
     * 모든 동물 카테고리 목록을 중복 제거하여 조회
     */
    @Query("SELECT DISTINCT p.animalCategory FROM Post p WHERE p.animalCategory IS NOT NULL ORDER BY p.animalCategory")
    List<String> findDistinctAnimalCategories();

    /**
     * 특정 동물 카테고리의 모든 품종 목록을 중복 제거하여 조회
     */
    @Query("SELECT DISTINCT p.animalBreed FROM Post p WHERE p.animalCategory = :animalCategory AND p.animalBreed IS NOT NULL ORDER BY p.animalBreed")
    List<String> findDistinctBreedsByAnimalCategory(@Param("animalCategory") String animalCategory);

    /**
     * 특정 지역의 게시글 수 조회
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.location = :location")
    long countByLocation(@Param("location") String location);

    /**
     * 활성 상태 게시글만 조회
     */
    List<Post> findByStatusOrderByCreatedAtDesc(Post.Status status);

    /**
     * 특정 게시글 타입의 활성 게시글 조회
     */
    List<Post> findByPostTypeAndStatusOrderByCreatedAtDesc(Post.PostType postType, Post.Status status);

    /**
     * 특정 사용자의 게시글 조회
     */
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}