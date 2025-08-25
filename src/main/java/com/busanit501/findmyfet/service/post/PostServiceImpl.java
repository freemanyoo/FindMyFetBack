package com.busanit501.findmyfet.service.post;

import com.busanit501.findmyfet.domain.post.Image;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.post.PostCreateRequestDto;
import com.busanit501.findmyfet.dto.post.PostDetailResponseDto;
import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.busanit501.findmyfet.dto.post.PostUpdateRequestDto;
import com.busanit501.findmyfet.repository.ImageRepository;
import com.busanit501.findmyfet.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // 생성자 자동주입
@Log4j2
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final FileUploadService fileUploadService;

    // 전체 게시글리스트 조회기능
    @Override
    @Transactional(readOnly = true) // 조회 기능이므로 readOnly=true로 성능 최적화
    public List<PostListResponseDto> findAllPosts() {
        return postRepository.findAll().stream() // DB에서 모든 Post를 가져와서
                .map(PostListResponseDto::new)      // DTO로 변환하고
                .collect(Collectors.toList());      // List로 만든다.
    }

    // 게시글 등록기능
    @Override
    @Transactional
    public Long createPost(PostCreateRequestDto requestDto, List<MultipartFile> images) {
        // 1-1. DTO를 Post 엔티티로 변환 후 저장
        Post post = requestDto.toEntity();
        Post savedPost = postRepository.save(post);
        log.info("Saved Post: {}", savedPost.getId());//

        if (images != null && !images.isEmpty()) {
            for (MultipartFile imageFile : images) {
                // 2-1. 실제 파일 업로드 로직 호출
                String storedFilename = fileUploadService.upload(imageFile);
                log.info("Uploaded Image: {}", storedFilename);

                if (storedFilename != null) {
                    // 2-2. Image 엔티티 생성
                    Image image = Image.builder()
                            .imageUrl(storedFilename)
                            .build();

                    // 2-3. 연관관계 설정 (Post -> Image)
                    savedPost.addImage(image);

                    // 2-4. Image 엔티티 저장 (Post에 Cascade 설정이 되어 있지만, 명시적으로 저장하는 것이 안전할 수 있음)
                    // CascadeType.ALL 이므로 Post 저장 시 Image도 함께 저장됩니다.

                }
            }
        }
        return savedPost.getId();
    }

    // 상세조회기능
    @Override
    @Transactional(readOnly = true)
    public PostDetailResponseDto findPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));
        return new PostDetailResponseDto(post);
    }

    // 삭제기능
    public void deletePost(Long postId) {
        // 1. 게시글 ID로 Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

        // 2. 연관된 이미지 파일들을 서버에서 삭제
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            for (Image image : post.getImages()) {
                String storedFilename = image.getImageUrl();
                log.info("Deleting Image File: {}", storedFilename);
                fileUploadService.delete(storedFilename);
            }
        }

        // 3. Post 엔티티 삭제
        // Post 엔티티에 cascade = CascadeType.ALL, orphanRemoval = true 설정이 되어 있으므로,
        // Post를 삭제하면 연관된 Image 엔티티들도 DB에서 함께 삭제됩니다.
        postRepository.delete(post);
        log.info("Deleted Post ID: {}", postId);
    }

    // 게시글 수정기능
    @Override
    @Transactional
    public void updatePost(Long postId, PostUpdateRequestDto requestDto, List<MultipartFile> newImages) {
        // 1. 게시글 ID로 Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. " + postId));
        log.info("Updating Post ID: {}", postId);


        // 2. 기존 이미지 파일 삭제 (간단한 버전: 모든 기존 이미지를 삭제하고 새로 추가)
        if (requestDto.getDeletedImageIds() != null && !requestDto.getDeletedImageIds().isEmpty()) {
            // DB에서 조회한 기존 이미지들의 ID를 Set으로 만들어 빠른 조회를 가능하게 함
            Set<Long> deleteIds = new HashSet<>(requestDto.getDeletedImageIds());

            // Iterator를 사용하여 컬렉션을 순회하면서 안전하게 원소를 제거
            Iterator<Image> iterator = post.getImages().iterator();
            while (iterator.hasNext()) {
                Image image = iterator.next();
                if (deleteIds.contains(image.getId())) {
                    // 2-1. 실제 파일 시스템에서 이미지 파일 삭제
                    fileUploadService.delete(image.getImageUrl());
                    log.info("Deleted image file: {}", image.getImageUrl());

                    // 2-2. 컬렉션에서 Image 엔티티 제거 (orphanRemoval=true에 의해 DB에서도 삭제됨)
                    iterator.remove();
                }
            }
        }
//        post.getImages().clear()가 호출되면, @OneToMany에 설정된 orphanRemoval = true 옵션 덕분에 부모(Post)와의 관계가 끊어진 Image 엔티티들(고아 객체)이 DB에서도 자동으로 삭제


        // 3. 텍스트 정보 업데이트(JPA 더티 체킹 활용)
            // -> 트랜잭션이 끝날 때 변경된 내용을 감지하여 자동으로 UPDATE 쿼리를 실행
        post.update(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getAnimalName(),
                requestDto.getLatitude(),
                requestDto.getLongitude()
        );

//        더티 체킹 (Dirty Checking):
//        @Transactional 환경에서 postRepository.findById()로 조회된 post 엔티티는 JPA의 영속성 컨텍스트에 의해 관리됩니다.
//        이 상태에서 post.update(...)와 같이 객체의 상태(필드 값)를 변경하면, 트랜잭션이 끝나는 시점에 JPA가 "어? 처음 조회했을 때랑 지금이랑 상태가 다르네?"라고 감지합니다.
//        이 '더러워진(dirty)' 객체를 발견하면, JPA가 자동으로 UPDATE 쿼리를 생성하여 데이터베이스에 반영해줍니다.
//        따라서 postRepository.save(post)를 다시 호출할 필요가 없어 코드가 간결해집니다.


        // 4. 새로운 이미지 파일 추가
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile imageFile : newImages) {
                String storedFilename = fileUploadService.upload(imageFile);
                if (storedFilename != null) {
                    Image image = Image.builder().imageUrl(storedFilename).build();
                    post.addImage(image);
                    log.info("Added new image: {}", storedFilename);
                }
            }
        }
        // postRepository.save(post)를 호출할 필요가 없습니다.
        // 트랜잭션 커밋 ->  더티 체킹 -> post가 자동으로 DB에 반영
    }

    @Override
    @Transactional
    public void completePost(Long postId) {
        // 1. 게시글 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));
        log.info("Completing Post ID: {}", postId);

        // 2. 게시글 상태를 'COMPLETED'로 변경
        post.complete();

        // 3. 더티 체킹에 의해 트랜잭션 종료 시 자동으로 UPDATE 쿼리 실행됨
    }
}