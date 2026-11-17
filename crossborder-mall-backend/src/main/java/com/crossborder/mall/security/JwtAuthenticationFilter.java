package com.crossborder.mall.security;

import com.crossborder.mall.dto.UserDTO;
import com.crossborder.mall.utils.RedisConstants;
import com.crossborder.mall.utils.UserHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token) && jwtTokenProvider.validate(token) && !isBlacklisted(token)) {
                Long userId = jwtTokenProvider.getUserId(token);
                String nickName = jwtTokenProvider.getNickName(token);
                String role = jwtTokenProvider.getRole(token);

                UserDTO user = new UserDTO();
                user.setId(userId);
                user.setNickName(nickName);
                user.setRole(role);
                UserHolder.saveUser(user);

                List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                if ("ADMIN".equalsIgnoreCase(role)) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } finally {
            UserHolder.removeUser();
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        // 兼容旧前端 sessionStorage + authorization 直接塞 token
        String raw = request.getHeader("authorization");
        if (StringUtils.hasText(raw) && !raw.startsWith("Bearer ")) {
            return raw;
        }
        return null;
    }

    private boolean isBlacklisted(String token) {
        Boolean exists = stringRedisTemplate.hasKey(RedisConstants.JWT_BLACKLIST_KEY + token);
        return Boolean.TRUE.equals(exists);
    }
}
