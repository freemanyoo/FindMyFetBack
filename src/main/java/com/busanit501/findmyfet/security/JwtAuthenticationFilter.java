package com.busanit501.findmyfet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 공개 경로는 필터 자체를 타지 않게 하고,
 * Authorization 헤더가 없으면 굳이 인증 시도하지 않고 통과시킵니다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final AntPathMatcher PATH = new AntPathMatcher();

    // ✅ 공개 엔드포인트(메서드 무관, 프리플라이트 포함) — 필요한 것만 남겨도 됨
    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/api/find-pets/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 프리플라이트는 스킵
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        // 공개 경로는 스킵(메서드 무관)
        for (String p : PUBLIC_PATTERNS) {
            if (PATH.match(p, uri)) return true;
        }

        // Authorization 없으면 스킵(굳이 검사 X)
        String auth = request.getHeader("Authorization");
        return (auth == null || !auth.startsWith("Bearer "));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // 이 시점은 공개 경로도 아니고, Authorization: Bearer ... 가 '있는' 요청입니다.
        try {
            String auth = request.getHeader("Authorization");
            String token = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;

            // TODO: 여기에 기존 토큰 검증/컨텍스트 세팅 로직을 넣으세요.
            //  ex)
            //  if (jwtProvider.validate(token)) {
            //      Authentication authentication = jwtProvider.getAuthentication(token);
            //      SecurityContextHolder.getContext().setAuthentication(authentication);
            //  }

        } catch (Exception e) {
            log.debug("JWT filter exception: {}", e.getMessage());
            // 보호된 자원 접근 시 최종 401/403 처리는 Security가 판단합니다.
        }

        filterChain.doFilter(request, response);
    }
}
