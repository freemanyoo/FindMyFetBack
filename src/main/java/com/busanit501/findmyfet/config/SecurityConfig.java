package com.busanit501.findmyfet.config;

import com.busanit501.findmyfet.security.JwtAuthenticationFilter; // 사용 안 하면 이 줄과 아래 addFilterBefore 삭제
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 사용 안 하면 제거

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger(있다면) 공개
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()

                        // 인증/회원가입 등 공개(있다면)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 기존 열어둔 posts (있다면)
                        .requestMatchers("/api/posts/**").permitAll()

                        // ✅ 검색/필터 API GET 공개
                        .requestMatchers(HttpMethod.GET, "/api/find-pets/**").permitAll()

                        // 그 외는 인증
                        .anyRequest().authenticated()
                );

        // JWT 필터 (사용 중일 때만)
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ allowCredentials=true 를 쓰면서 * 를 쓰면 안 됨 → allowedOriginPatterns 또는 명시 오리진 사용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));
        conf.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        conf.setAllowedHeaders(List.of("*"));
        conf.setExposedHeaders(List.of("Authorization","Content-Disposition"));
        conf.setAllowCredentials(true); // 쿠키/인증헤더가 필요 없으면 false로 바꿔도 OK

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }

    // 이미 다른 곳에 있으면 중복 제거
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
