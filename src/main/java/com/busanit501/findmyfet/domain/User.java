package com.busanit501.findmyfet.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User extends BaseEntity { // 베이스 엔티티 상속

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 키 자동생성
    @Column(name = "user_id") // DB 컬럼명을 명시적으로 지정하는 것이 좋습니다.
    private Long userId;

    @Column(unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
