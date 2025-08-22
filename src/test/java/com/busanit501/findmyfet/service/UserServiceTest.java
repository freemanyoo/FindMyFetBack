package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.dto.UserSignupRequestDTO;
import com.busanit501.findmyfet.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository; // 가짜 UserRepository

    @Mock
    private PasswordEncoder passwordEncoder; // 가짜 PasswordEncoder

    @InjectMocks
    private UserService userService; // 가짜 객체들을 주입받을 테스트 대상

    @Test
    @DisplayName("회원가입 성공 테스트")
    void 회원가입_성공() {
        log.info("회원가입 성공 테스트 시작 >>>>>>>>>>");
        // given (테스트 조건 설정)
        UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                .loginId("testuser")
                .password("password123")
                .name("테스트유저")
                .build();

        // userRepository.findByLoginId가 호출되면, 빈 Optional 객체를 반환하도록 설정 (중복 아이디 없음)
        given(userRepository.findByLoginId(requestDTO.getLoginId())).willReturn(Optional.empty());

        // passwordEncoder.encode가 호출되면, "encodedPassword" 문자열을 반환하도록 설정
        given(passwordEncoder.encode(requestDTO.getPassword())).willReturn("encodedPassword");

        // when (테스트 실행)
        userService.signup(requestDTO);

        // then (결과 검증)
        // userRepository.save 메서드가 User 객체를 인자로 받아 한 번이라도 호출되었는지 검증
        verify(userRepository).save(any(User.class));
        log.info("회원가입 성공 테스트 종료 >>>>>>>>>>");
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 아이디 중복")
    void 회원가입_실패_아이디_중복() {
        // given (테스트 조건 설정)
        UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                .loginId("existingUser")
                .password("password123")
                .name("기존유저")
                .build();

        // userRepository.findByLoginId가 호출되면, 이미 존재하는 User 객체를 포함한 Optional을 반환하도록 설정
        given(userRepository.findByLoginId(requestDTO.getLoginId())).willReturn(Optional.of(new User()));

        // when & then (테스트 실행 및 결과 검증)
        // userService.signup을 실행했을 때 IllegalArgumentException이 발생하는지 검증
        assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(requestDTO);
        });

        // userRepository.save 메서드가 절대로 호출되지 않았는지 검증
        verify(userRepository, never()).save(any(User.class));
    }

}
