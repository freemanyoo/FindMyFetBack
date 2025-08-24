package com.busanit501.findmyfet.service.post;

import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.busanit501.findmyfet.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true) // 조회 기능이므로 readOnly=true로 성능 최적화
    public List<PostListResponseDto> findAllPosts() {
        return postRepository.findAll().stream() // DB에서 모든 Post를 가져와서
                .map(PostListResponseDto::new)      // DTO로 변환하고
                .collect(Collectors.toList());      // List로 만든다.
    }
}