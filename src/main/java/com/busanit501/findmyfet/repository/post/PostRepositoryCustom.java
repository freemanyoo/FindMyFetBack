package com.busanit501.findmyfet.repository.post;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.paging.PageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    // 페이징처리 동적 검색 쿼리를 처리할 메소드
    Page<Post> search(PageRequestDTO pageRequestDTO, Pageable pageable);

}
