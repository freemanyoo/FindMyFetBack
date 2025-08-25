package com.busanit501.findmyfet.domain;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
public abstract class BaseEntity {

    @CreatedDate // postRepository.save()가 호출되어 새로운 데이터가 DB에 처음 저장되는 시점에 Spring Data JPA가 현재 시간을 자동으로
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime createdAt; //변수명 변경_240824

    @LastModifiedDate
    @Column(name = "mod_date")
    private LocalDateTime updatedAt; //변수명 변경_240824
}
