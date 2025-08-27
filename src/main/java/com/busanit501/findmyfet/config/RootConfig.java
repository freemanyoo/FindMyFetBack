package com.busanit501.findmyfet.config;

import com.busanit501.findmyfet.domain.post.AnimalGender;
import com.busanit501.findmyfet.domain.post.Image;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.post.MyPostResponseDto;
import com.busanit501.findmyfet.dto.post.PostDetailResponseDto;
import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

// 작업 순서2
@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);


        modelMapper.createTypeMap(Post.class, MyPostResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(Post::getId, MyPostResponseDto::setPostId);
                });

        // Post -> PostDetailResponseDto 매핑 설정
        modelMapper.createTypeMap(Post.class, PostDetailResponseDto.class)
                .addMappings(mapper -> {
                    // postId는 필드명이 id -> postId 이므로 수동 매핑
                    mapper.map(Post::getId, PostDetailResponseDto::setPostId);

                    // AnimalGender Enum -> String("수컷", "암컷") 변환
                    mapper.using((Converter<AnimalGender, String>) context ->
                            context.getSource() == null ? null : context.getSource().getDescription()
                    ).map(Post::getGender, PostDetailResponseDto::setGender);

                    // List<Image> -> List<String> (URL 목록) 변환
                    mapper.using((Converter<List<Image>, List<String>>) context ->
                            context.getSource() == null ? null : context.getSource().stream()
                                    .map(Image::getImageUrl)
                                    .collect(Collectors.toList())
                    ).map(Post::getImages, PostDetailResponseDto::setImageUrls);
                });

        // Post -> PostListResponseDto 매핑 설정
        modelMapper.createTypeMap(Post.class, PostListResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(Post::getId, PostListResponseDto::setPostId);

                    // 첫 번째 이미지를 썸네일로 설정
                    mapper.using((Converter<List<Image>, String>) context ->
                            (context.getSource() == null || context.getSource().isEmpty()) ?
                                    null : context.getSource().get(0).getImageUrl()
                    ).map(Post::getImages, PostListResponseDto::setThumbnailUrl);
                });

        return modelMapper;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}