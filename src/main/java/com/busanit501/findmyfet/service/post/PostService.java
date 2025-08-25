package com.busanit501.findmyfet.service.post;


import com.busanit501.findmyfet.dto.post.PostCreateRequestDto;
import com.busanit501.findmyfet.dto.post.PostDetailResponseDto;
import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.busanit501.findmyfet.dto.post.PostUpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    // 게시판 전체조회
    List<PostListResponseDto> findAllPosts();

    /**
     * 게시글 상세 조회
     * @param postId 조회할 게시글 ID
     * @return 게시글 상세 정보 DTO
     */
    PostDetailResponseDto findPostById(Long postId);

    /**
     * 게시글 등록
     * @param requestDto 게시글 정보 DTO
     * @param images 첨부 이미지 파일 리스트
     * @return 등록된 게시글 ID
     */
    Long createPost(PostCreateRequestDto requestDto, List<MultipartFile> images);

    /**
     * 게시글 삭제
     * @param postId 삭제할 게시글 ID
     */
    void deletePost(Long postId);


    /**
     * 게시글 수정
     * @param postId 수정할 게시글 ID
     * @param requestDto 수정할 게시글 정보 DTO
     * @param newImages 새로 추가할 이미지 파일 리스트
     */
    void updatePost(Long postId, PostUpdateRequestDto requestDto, List<MultipartFile> newImages);


    /**
     * 게시글 찾기 완료 처리
     * @param postId 완료 처리할 게시글 ID
     */
    void completePost(Long postId);

}