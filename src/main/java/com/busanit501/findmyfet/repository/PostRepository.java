package com.busanit501.findmyfet.repository;


import com.busanit501.findmyfet.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
