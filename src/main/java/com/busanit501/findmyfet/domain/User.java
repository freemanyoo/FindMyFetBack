package com.busanit501.findmyfet.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class User extends BaseEntity { // 베이스 엔티티 상속
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 키 자동생성
    private Long userid;

    @Column(unique = true, nullable = false)
    private String loginid;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone_number;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


}
