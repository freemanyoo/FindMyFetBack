package com.busanit501.findmyfet.service.post;


import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import java.util.List;

public interface PostService {

    List<PostListResponseDto> findAllPosts();
}