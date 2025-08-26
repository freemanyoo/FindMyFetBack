package com.busanit501.findmyfet.dto.user;

import com.busanit501.findmyfet.domain.User;
import lombok.Getter;

@Getter
public class AuthorDto {
    private Long userId;
    private String name;

    public AuthorDto(User user) {
        this.userId = user.getUserid();
        this.name = user.getName();
    }
}
